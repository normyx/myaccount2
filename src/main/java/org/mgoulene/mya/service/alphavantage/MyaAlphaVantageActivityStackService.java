package org.mgoulene.mya.service.alphavantage;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.cryptocurrency.response.CryptoResponse;
import com.crazzyghost.alphavantage.cryptocurrency.response.CryptoUnit;
import com.crazzyghost.alphavantage.forex.response.ForexResponse;
import com.crazzyghost.alphavantage.forex.response.ForexUnit;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.mgoulene.domain.enumeration.Currency;
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MyaAlphaVantageActivityStackService {

    private ActivityTimerTask activityTimer;
    private Timer timer;

    @Value("${alphavantage.loop.every}")
    private int loopEvery;

    @Value("${alphavantage.loop.iteration}")
    private int iteration;

    private final MyaStockPortfolioItemService stockPortfolioItemService;

    public MyaAlphaVantageActivityStackService(MyaStockPortfolioItemService stockPortfolioItemService) {
        this.stockPortfolioItemService = stockPortfolioItemService;
    }

    @PostConstruct
    private void init() {
        activityTimer = new ActivityTimerTask(stockPortfolioItemService, iteration);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(activityTimer, 0, loopEvery * 1000);
    }

    public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
        activityTimer.add(type, stockPortfolioItemDTO);
    }

    public enum ActivityType {
        UPDATE_STOCK_PRICES,
        UPDATE_STOCK_CURRENCIES,
        UPDATE_CRYPTO,
    }

    private class ActivityTimerTask extends TimerTask {

        private final Logger log = LoggerFactory.getLogger(ActivityTimerTask.class);
        private Stack<Activity> activities = new Stack<>();
        private final MyaStockPortfolioItemService stockPortfolioItemService;

        @Value("${alphavantage.key}")
        private String key;

        private int iter;

        public ActivityTimerTask(MyaStockPortfolioItemService stockPortfolioItemService, int iteration) {
            this.stockPortfolioItemService = stockPortfolioItemService;
            this.iter = iteration;
        }

        @PostConstruct
        private void init() {
            Config cfg = Config.builder().key(key).timeOut(10).build();
            AlphaVantage.api().init(cfg);
        }

        public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
            activities.push(new Activity(type, stockPortfolioItemDTO));
        }

        private void updateCrypto(StockPortfolioItemDTO stockPortfolioItemDTO) {
            CryptoResponse cryptoResponse = AlphaVantage
                .api()
                .crypto()
                .daily()
                .market(Currency.EUR.name())
                .forSymbol(stockPortfolioItemDTO.getStockSymbol())
                .fetchSync();
            CryptoUnit moreRecentCryptoUnit = null;
            for (CryptoUnit cu : cryptoResponse.getCryptoUnits()) {
                LocalDate cuDate = LocalDate.parse(cu.getDate());
                if (moreRecentCryptoUnit == null) {
                    moreRecentCryptoUnit = cu;
                } else {
                    LocalDate mrcuDate = LocalDate.parse(moreRecentCryptoUnit.getDate());

                    if (cuDate.isAfter(mrcuDate)) {
                        moreRecentCryptoUnit = cu;
                    }
                }
                if (stockPortfolioItemDTO.getStockAcquisitionDate().isEqual(cuDate)) {
                    stockPortfolioItemDTO.setStockPriceAtAcquisitionDate((float) cu.getClose());
                }
            }
            stockPortfolioItemDTO.setStockCurrentDate(LocalDate.parse(moreRecentCryptoUnit.getDate()));
            stockPortfolioItemDTO.setStockCurrentPrice((float) moreRecentCryptoUnit.getClose());
            stockPortfolioItemDTO.setLastStockUpdate(Instant.now());
            stockPortfolioItemService.save(stockPortfolioItemDTO);
            log.debug("Updating updateCrypto : {}", stockPortfolioItemDTO);
        }

        private void updateStockPrices(StockPortfolioItemDTO stockPortfolioItemDTO) {
            TimeSeriesResponse response = AlphaVantage
                .api()
                .timeSeries()
                .daily()
                .adjusted()
                .forSymbol(stockPortfolioItemDTO.getStockSymbol())
                .outputSize(OutputSize.FULL)
                .fetchSync();
            StockUnit moreRecentStockUnit = null;
            for (StockUnit su : response.getStockUnits()) {
                LocalDate suDate = LocalDate.parse(su.getDate());
                if (moreRecentStockUnit == null) {
                    moreRecentStockUnit = su;
                } else {
                    LocalDate mrsuDate = LocalDate.parse(moreRecentStockUnit.getDate());

                    if (suDate.isAfter(mrsuDate)) {
                        moreRecentStockUnit = su;
                    }
                }
                if (stockPortfolioItemDTO.getStockAcquisitionDate().isEqual(suDate)) {
                    stockPortfolioItemDTO.setStockPriceAtAcquisitionDate((float) su.getAdjustedClose());
                }
            }

            stockPortfolioItemDTO.setStockCurrentDate(LocalDate.parse(moreRecentStockUnit.getDate()));
            stockPortfolioItemDTO.setStockCurrentPrice((float) moreRecentStockUnit.getAdjustedClose());
            stockPortfolioItemDTO.setLastStockUpdate(Instant.now());
            stockPortfolioItemService.save(stockPortfolioItemDTO);
            log.debug("Updating updateStockPrices : {}", stockPortfolioItemDTO);
        }

        private void updateStockCurrencies(StockPortfolioItemDTO stockPortfolioItemDTO) {
            if (stockPortfolioItemDTO.getStockCurrency() != Currency.EUR) {
                ForexResponse forexResponse = AlphaVantage
                    .api()
                    .forex()
                    .daily()
                    .fromSymbol(stockPortfolioItemDTO.getStockCurrency().name())
                    .toSymbol(Currency.EUR.name())
                    .outputSize(OutputSize.FULL)
                    .fetchSync();
                ForexUnit moreRecentForexUnit = null;
                for (ForexUnit fu : forexResponse.getForexUnits()) {
                    LocalDate fuDate = LocalDate.parse(fu.getDate());
                    if (moreRecentForexUnit == null) {
                        moreRecentForexUnit = fu;
                    } else {
                        LocalDate mrfuDate = LocalDate.parse(moreRecentForexUnit.getDate());
                        if (fuDate.isAfter(mrfuDate)) {
                            moreRecentForexUnit = fu;
                        }
                    }
                    if (stockPortfolioItemDTO.getStockAcquisitionDate().isEqual(fuDate)) {
                        stockPortfolioItemDTO.setStockAcquisitionCurrencyFactor((float) fu.getClose());
                    }
                }
                stockPortfolioItemDTO.setStockCurrentCurrencyFactor((float) moreRecentForexUnit.getClose());
            } else {
                stockPortfolioItemDTO.setStockCurrentCurrencyFactor((float) 1);
                stockPortfolioItemDTO.setStockAcquisitionCurrencyFactor((float) 1);
            }
            stockPortfolioItemDTO.setLastCurrencyUpdate(Instant.now());
            stockPortfolioItemService.save(stockPortfolioItemDTO);
            log.debug("Updating updateStockCurrencies : {}", stockPortfolioItemDTO);
        }

        @Override
        public void run() {
            for (int i = 0; i < iter; i++) {
                if (!activities.empty()) {
                    Activity activity = activities.pop();
                    StockPortfolioItemDTO stockPortfolioItemDTO = activity.getStockPortfolioItemDTO();
                    switch (activity.getActivityType()) {
                        case UPDATE_STOCK_PRICES:
                            updateStockPrices(stockPortfolioItemDTO);
                            break;
                        case UPDATE_STOCK_CURRENCIES:
                            updateStockCurrencies(stockPortfolioItemDTO);
                            break;
                        case UPDATE_CRYPTO:
                            updateCrypto(stockPortfolioItemDTO);
                            break;
                    }
                }
            }
        }
    }

    private class Activity {

        private ActivityType activityType;
        private StockPortfolioItemDTO stockPortfolioItemDTO;

        public Activity(ActivityType activityType, StockPortfolioItemDTO stockPortfolioItemDTO) {
            this.activityType = activityType;
            this.stockPortfolioItemDTO = stockPortfolioItemDTO;
        }

        public ActivityType getActivityType() {
            return activityType;
        }

        public StockPortfolioItemDTO getStockPortfolioItemDTO() {
            return stockPortfolioItemDTO;
        }
    }
}
