package org.mgoulene.mya.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.domain.enumeration.Currency;
import org.mgoulene.domain.enumeration.StockType;
import org.mgoulene.mya.repository.MyaStockMarketDataRepository;
import org.mgoulene.mya.repository.MyaStockPortfolioItemRepository;
import org.mgoulene.mya.service.dto.MyaDateDataStockPoints;
import org.mgoulene.mya.service.dto.MyaStockDataList;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.repository.search.StockPortfolioItemSearchRepository;
import org.mgoulene.service.StockPortfolioItemService;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.service.mapper.StockPortfolioItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockPortfolioItem}.
 */
@Service
@Transactional
public class MyaStockPortfolioItemService extends StockPortfolioItemService {

    private final Logger log = LoggerFactory.getLogger(MyaStockPortfolioItemService.class);

    private final MyaStockPortfolioItemRepository myaStockPortfolioItemRepository;
    private final MyaStockMarketDataRepository myaStockMarketDataRepository;

    public MyaStockPortfolioItemService(
        StockPortfolioItemRepository stockPortfolioItemRepository,
        StockPortfolioItemMapper stockPortfolioItemMapper,
        StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository,
        MyaStockPortfolioItemRepository myaStockPortfolioItemRepository,
        MyaStockMarketDataRepository myaStockMarketDataRepository
    ) {
        super(stockPortfolioItemRepository, stockPortfolioItemMapper, stockPortfolioItemSearchRepository);
        this.myaStockPortfolioItemRepository = myaStockPortfolioItemRepository;
        this.myaStockMarketDataRepository = myaStockMarketDataRepository;
    }

    /**
     * Get all the stockPortfolioItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockPortfolioItemDTO> findAll() {
        log.debug("Request to get all StockPortfolioItems");
        return stockPortfolioItemRepository
            .findAll()
            .stream()
            .map(stockPortfolioItemMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    List<StockPortfolioItemDTO> findAllWithSymbolAndApplicationUser(String symbol, Long applicationId) {
        return myaStockPortfolioItemRepository
            .findAllWithSymbolAndApplicationUser(symbol, applicationId)
            .stream()
            .map(stockPortfolioItemMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    List<StockPortfolioItemDTO> findAllWithApplicationUser(Long applicationId) {
        return myaStockPortfolioItemRepository
            .findAllWithApplicationUser(applicationId)
            .stream()
            .map(stockPortfolioItemMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Transactional(readOnly = true)
    public MyaDateDataStockPoints findDateDataPoints(Long applicationId) {
        log.debug("Request to get findDateDataPoints for applicationId : {}", applicationId);
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = findAllWithApplicationUser(applicationId);
        MyaDateDataStockPoints points = null;
        Map<String, MyaStockDataList> currenciesMap = new HashMap<>();
        for (StockPortfolioItemDTO stockPortfolioItemDTO : stockPortfolioItemDTOs) {
            String symbolStockMarket = stockPortfolioItemDTO.getStockSymbol();
            Currency currencyStockMarket = stockPortfolioItemDTO.getStockCurrency();
            if (stockPortfolioItemDTO.getStockType() == StockType.CRYPTO) {
                symbolStockMarket += Currency.EUR.name();
            }
            List<StockMarketData> stockMarketDatas = myaStockMarketDataRepository.findStockMarketData(symbolStockMarket);

            MyaStockDataList currencyDataList = null;
            if (stockPortfolioItemDTO.getStockCurrency() != Currency.EUR) {
                String currencySymbol = stockPortfolioItemDTO.getStockCurrency().name() + Currency.EUR.name();
                currencyDataList = currenciesMap.get(currencySymbol);
                if (currencyDataList == null) {
                    currencyDataList = new MyaStockDataList(myaStockMarketDataRepository.findStockMarketData(currencySymbol));
                    currenciesMap.put(currencySymbol, currencyDataList);
                }
            }
            if (currencyDataList != null) {
                Predicate<StockMarketData> filter = data ->
                    data.getDataDate().isAfter(stockPortfolioItemDTO.getStockAcquisitionDate()) ||
                    data.getDataDate().isEqual(stockPortfolioItemDTO.getStockAcquisitionDate());

                currencyDataList = new MyaStockDataList(currencyDataList.stream().filter(filter).collect(Collectors.toList()));
            }
            MyaDateDataStockPoints newPoints = new MyaDateDataStockPoints(
                stockMarketDatas,
                symbolStockMarket,
                currencyStockMarket,
                stockPortfolioItemDTO.getStockSharesNumber(),
                stockPortfolioItemDTO.getStockAcquisitionDate(),
                stockPortfolioItemDTO.getStockAcquisitionPrice(),
                currencyDataList
            );

            if (points == null) {
                points = newPoints;
            } else {
                points.merge(newPoints);
            }
        }

        return points;
    }

    @Transactional(readOnly = true)
    public MyaDateDataStockPoints findSymbolDateDataPoints(Long applicationId, String symbol) {
        log.debug("Request to get findDateDataPoints for applicationId : {}", applicationId);
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = findAllWithApplicationUser(applicationId);
        MyaDateDataStockPoints points = null;
        Map<String, MyaStockDataList> currenciesMap = new HashMap<>();
        for (StockPortfolioItemDTO stockPortfolioItemDTO : stockPortfolioItemDTOs) {
            String symbolStockMarket = stockPortfolioItemDTO.getStockSymbol();
            if (symbolStockMarket.equals(symbol)) {
                Currency currencyStockMarket = stockPortfolioItemDTO.getStockCurrency();
                if (stockPortfolioItemDTO.getStockType() == StockType.CRYPTO) {
                    symbolStockMarket += Currency.EUR.name();
                }
                List<StockMarketData> stockMarketDatas = myaStockMarketDataRepository.findStockMarketData(symbolStockMarket);

                MyaStockDataList currencyDataList = null;
                if (stockPortfolioItemDTO.getStockCurrency() != Currency.EUR) {
                    String currencySymbol = stockPortfolioItemDTO.getStockCurrency().name() + Currency.EUR.name();
                    currencyDataList = currenciesMap.get(currencySymbol);
                    if (currencyDataList == null) {
                        currencyDataList = new MyaStockDataList(myaStockMarketDataRepository.findStockMarketData(currencySymbol));
                        currenciesMap.put(currencySymbol, currencyDataList);
                    }
                }
                if (currencyDataList != null) {
                    Predicate<StockMarketData> filter = data ->
                        data.getDataDate().isAfter(stockPortfolioItemDTO.getStockAcquisitionDate()) ||
                        data.getDataDate().isEqual(stockPortfolioItemDTO.getStockAcquisitionDate());

                    currencyDataList = new MyaStockDataList(currencyDataList.stream().filter(filter).collect(Collectors.toList()));
                }
                MyaDateDataStockPoints newPoints = new MyaDateDataStockPoints(
                    stockMarketDatas,
                    symbolStockMarket,
                    currencyStockMarket,
                    stockPortfolioItemDTO.getStockSharesNumber(),
                    stockPortfolioItemDTO.getStockAcquisitionDate(),
                    stockPortfolioItemDTO.getStockAcquisitionPrice(),
                    currencyDataList
                );

                if (points == null) {
                    points = newPoints;
                } else {
                    points.merge(newPoints);
                }
            }
        }

        return points;
    }
}
