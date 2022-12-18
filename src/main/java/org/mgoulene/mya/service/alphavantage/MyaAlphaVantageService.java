package org.mgoulene.mya.service.alphavantage;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.mgoulene.domain.enumeration.Currency;
import org.mgoulene.domain.enumeration.StockType;
import org.mgoulene.mya.service.MyaStockMarketDataService;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.mya.service.alphavantage.MyaAlphaVantageActivityStackService.ActivityType;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyaAlphaVantageService {

    private final Logger log = LoggerFactory.getLogger(MyaAlphaVantageService.class);

    private final MyaStockPortfolioItemService stockPortfolioItemService;

    private final MyaAlphaVantageActivityStackService myaAlphaVantageActivityStackService;

    private final MyaStockMarketDataService myaStockMarketDataService;

    public MyaAlphaVantageService(
        MyaStockPortfolioItemService stockPortfolioItemService,
        MyaAlphaVantageActivityStackService myaAlphaVantageActivityStackService,
        MyaStockMarketDataService myaStockMarketDataService
    ) {
        this.stockPortfolioItemService = stockPortfolioItemService;
        this.myaAlphaVantageActivityStackService = myaAlphaVantageActivityStackService;
        this.myaStockMarketDataService = myaStockMarketDataService;
    }

    public void updateStockPortfolioItem(StockPortfolioItemDTO dto) {
        log.debug("Service to Update StockPortfolioItem : {}", dto);

        Optional<StockMarketDataDTO> acquisitionData;
        Optional<StockMarketDataDTO> lastData;
        Optional<StockMarketDataDTO> acquisitionCurrencyData;
        Optional<StockMarketDataDTO> lastCurrencyData;
        switch (dto.getStockType()) {
            case CRYPTO:
                acquisitionData =
                    myaStockMarketDataService.findStockMarketDataForDate(
                        dto.getStockSymbol() + Currency.EUR.name(),
                        dto.getStockAcquisitionDate()
                    );
                lastData = myaStockMarketDataService.findLastStockMarketData(dto.getStockSymbol() + Currency.EUR.name());
                dto.setStockAcquisitionCurrencyFactor(1f);
                dto.setStockCurrentCurrencyFactor(1f);
                if (acquisitionData.isPresent()) {
                    dto.setStockPriceAtAcquisitionDate(acquisitionData.get().getCloseValue());
                }
                if (lastData.isPresent()) {
                    dto.setStockCurrentDate(lastData.get().getDataDate());
                    dto.setStockCurrentPrice(lastData.get().getCloseValue());
                }
                break;
            case STOCK:
                acquisitionData = myaStockMarketDataService.findStockMarketDataForDate(dto.getStockSymbol(), dto.getStockAcquisitionDate());
                lastData = myaStockMarketDataService.findLastStockMarketData(dto.getStockSymbol());
                log.debug("Acquisition Data : {}", acquisitionData);
                log.debug("Current Data : {}", lastData);
                if (dto.getStockCurrency() != Currency.EUR) {
                    acquisitionCurrencyData =
                        myaStockMarketDataService.findStockMarketDataForDate(
                            dto.getStockCurrency().name() + Currency.EUR.name(),
                            dto.getStockAcquisitionDate()
                        );
                    lastCurrencyData =
                        myaStockMarketDataService.findLastStockMarketData(dto.getStockCurrency().name() + Currency.EUR.name());
                    log.debug("Current Data : {}", lastData);
                    if (acquisitionCurrencyData.isPresent()) {
                        dto.setStockAcquisitionCurrencyFactor(acquisitionCurrencyData.get().getCloseValue());
                    }
                    if (lastCurrencyData.isPresent()) {
                        dto.setStockCurrentCurrencyFactor(lastCurrencyData.get().getCloseValue());
                    }
                } else {
                    dto.setStockAcquisitionCurrencyFactor(1f);
                    dto.setStockCurrentCurrencyFactor(1f);
                }
                if (acquisitionData.isPresent()) {
                    dto.setStockPriceAtAcquisitionDate(acquisitionData.get().getCloseValue());
                }
                if (lastData.isPresent()) {
                    dto.setStockCurrentDate(lastData.get().getDataDate());
                    dto.setStockCurrentPrice(lastData.get().getCloseValue());
                }
                break;
        }
        dto.setLastCurrencyUpdate(Instant.now());
        stockPortfolioItemService.save(dto);
    }

    @Scheduled(fixedRateString = "${alphavantage.update-stock-portfolio-item-fixe-rate}")
    public void updateAllStockData() {
        log.debug("Service updateAllStockData");
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = stockPortfolioItemService.findAll();
        Map<String, Object> stockMarketData = new HashMap<String, Object>();
        Map<String, Object> currencyMarketData = new HashMap<String, Object>();
        Map<String, Object> cryptoMarketData = new HashMap<String, Object>();

        for (StockPortfolioItemDTO spi : stockPortfolioItemDTOs) {
            if (spi.getStockType() == StockType.STOCK) {
                stockMarketData.put(spi.getStockSymbol(), null);
                if (spi.getStockCurrency() != Currency.EUR) {
                    currencyMarketData.put(spi.getStockCurrency().name(), null);
                }
            } else if (spi.getStockType() == StockType.CRYPTO) {
                cryptoMarketData.put(spi.getStockSymbol(), null);
            }
        }
        for (String symbol : stockMarketData.keySet()) {
            myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_PRICES, symbol);
        }
        for (String symbol : currencyMarketData.keySet()) {
            myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_STOCK_CURRENCIES, symbol);
        }
        for (String symbol : cryptoMarketData.keySet()) {
            myaAlphaVantageActivityStackService.add(ActivityType.UPDATE_CRYPTO, symbol);
        }
    }

    @Scheduled(fixedRateString = "${alphavantage.update-scheduled-fixe-rate}")
    public void updateAllStockPortfolioItem() {
        List<StockPortfolioItemDTO> stockPortfolioItemDTOs = stockPortfolioItemService.findAll();
        for (StockPortfolioItemDTO spi : stockPortfolioItemDTOs) {
            this.updateStockPortfolioItem(spi);
        }
    }
}
