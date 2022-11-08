package org.mgoulene.mya.service.alphavantage;

import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.forex.response.ForexResponse;
import com.crazzyghost.alphavantage.forex.response.ForexUnit;
import com.crazzyghost.alphavantage.parameters.OutputSize;
import com.crazzyghost.alphavantage.timeseries.response.StockUnit;
import com.crazzyghost.alphavantage.timeseries.response.TimeSeriesResponse;
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

    private final MyaStockPortfolioItemService stockPortfolioItemService;

    public MyaAlphaVantageActivityStackService(MyaStockPortfolioItemService stockPortfolioItemService) {
        this.stockPortfolioItemService = stockPortfolioItemService;
    }

    @PostConstruct
    private void init() {
        activityTimer = new ActivityTimerTask(stockPortfolioItemService);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(activityTimer, 0, loopEvery * 1000);
    }

    public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
        activityTimer.add(type, stockPortfolioItemDTO);
    }

    public enum ActivityType {
        UPDATE_STOCK_PRICES,
        UPDATE_STOCK_CURRENCIES,
    }

    @Service
    private class ActivityTimerTask extends TimerTask {

        private final Logger log = LoggerFactory.getLogger(ActivityTimerTask.class);
        private Stack<Activity> activities = new Stack<>();
        private final MyaStockPortfolioItemService stockPortfolioItemService;

        @Value("${alphavantage.key}")
        private String key;

        @Value("${alphavantage.loop.iteration}")
        private int iter;

        public ActivityTimerTask(MyaStockPortfolioItemService stockPortfolioItemService) {
            this.stockPortfolioItemService = stockPortfolioItemService;
        }

        @PostConstruct
        private void init() {
            Config cfg = Config.builder().key(key).timeOut(10).build();
            AlphaVantage.api().init(cfg);
        }

        public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
            activities.push(new Activity(type, stockPortfolioItemDTO));
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
                    stockPortfolioItemDTO.setStockPriceAtAcquisitionDate((float) su.getClose());
                }
            }

            stockPortfolioItemDTO.setStockCurrentDate(LocalDate.parse(moreRecentStockUnit.getDate()));
            stockPortfolioItemDTO.setStockCurrentPrice((float) moreRecentStockUnit.getAdjustedClose());
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
