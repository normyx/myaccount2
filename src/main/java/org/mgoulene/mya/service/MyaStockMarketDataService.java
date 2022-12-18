package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.Optional;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.mya.repository.MyaStockMarketDataRepository;
import org.mgoulene.repository.StockMarketDataRepository;
import org.mgoulene.repository.search.StockMarketDataSearchRepository;
import org.mgoulene.service.StockMarketDataService;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.mgoulene.service.mapper.StockMarketDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link StockMarketData}.
 */
@Service
@Transactional
@Primary
public class MyaStockMarketDataService extends StockMarketDataService {

    private final Logger log = LoggerFactory.getLogger(MyaStockMarketDataService.class);

    private final MyaStockMarketDataRepository myaStockMarketDataRepository;

    private final MyaStockPortfolioItemService myaStockPortfolioItemService;

    public MyaStockMarketDataService(
        StockMarketDataRepository stockMarketDataRepository,
        StockMarketDataMapper stockMarketDataMapper,
        StockMarketDataSearchRepository stockMarketDataSearchRepository,
        MyaStockMarketDataRepository myaStockMarketDataRepository,
        MyaStockPortfolioItemService myaStockPortfolioItemService
    ) {
        super(stockMarketDataRepository, stockMarketDataMapper, stockMarketDataSearchRepository);
        this.myaStockMarketDataRepository = myaStockMarketDataRepository;
        this.myaStockPortfolioItemService = myaStockPortfolioItemService;
    }

    @Transactional(readOnly = true)
    public Optional<StockMarketDataDTO> findLastStockMarketData(String symbol) {
        log.debug("Request to get findLastStockMarketData : {}", symbol);
        return myaStockMarketDataRepository.findLastStockMarketData(symbol).map(stockMarketDataMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<StockMarketDataDTO> findStockMarketDataForDate(String symbol, LocalDate date) {
        log.debug("Request to get findLastStockMarketData : {}", symbol);
        return myaStockMarketDataRepository.findStockMarketDataForDate(symbol, date).map(stockMarketDataMapper::toDto);
    }
}
