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
import org.mgoulene.mya.service.MyaStockPortfolioItemService;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MyaAlphaVantageActivityStackService {

    private ActivityTimerTask activityTimer;
    private Timer timer;

    public MyaAlphaVantageActivityStackService(MyaStockPortfolioItemService stockPortfolioItemService) {
        activityTimer = new ActivityTimerTask(stockPortfolioItemService);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(activityTimer, 0, 61 * 1000);
    }

    public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
        activityTimer.add(type, stockPortfolioItemDTO);
    }

    public enum ActivityType {
        UPDATE_STOCK_CURRENT,
        UPDATE_STOCK_CURRENT_CURRENCY,
    }

    @Service
    private class ActivityTimerTask extends TimerTask {

        private final Logger log = LoggerFactory.getLogger(ActivityTimerTask.class);
        private Stack<Activity> activities = new Stack<>();
        private final MyaStockPortfolioItemService stockPortfolioItemService;

        public ActivityTimerTask(MyaStockPortfolioItemService stockPortfolioItemService) {
            this.stockPortfolioItemService = stockPortfolioItemService;
            Config cfg = Config.builder().key("AFVQ4M4A8XB1IR6Q").timeOut(10).build();
            AlphaVantage.api().init(cfg);
        }

        public void add(ActivityType type, StockPortfolioItemDTO stockPortfolioItemDTO) {
            activities.push(new Activity(type, stockPortfolioItemDTO));
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                if (!activities.empty()) {
                    Activity activity = activities.pop();
                    StockPortfolioItemDTO stockPortfolioItemDTO = activity.getStockPortfolioItemDTO();
                    switch (activity.getActivityType()) {
                        case UPDATE_STOCK_CURRENT:
                            TimeSeriesResponse response = AlphaVantage
                                .api()
                                .timeSeries()
                                .daily()
                                .adjusted()
                                .forSymbol(stockPortfolioItemDTO.getStockSymbol())
                                .outputSize(OutputSize.COMPACT)
                                .fetchSync();
                            StockUnit moreRecentStockUnit = null;
                            for (StockUnit su : response.getStockUnits()) {
                                if (moreRecentStockUnit == null) {
                                    moreRecentStockUnit = su;
                                } else {
                                    LocalDate mrsuDate = LocalDate.parse(moreRecentStockUnit.getDate());
                                    LocalDate suDate = LocalDate.parse(su.getDate());
                                    if (suDate.isAfter(mrsuDate)) {
                                        moreRecentStockUnit = su;
                                    }
                                }
                            }
                            stockPortfolioItemDTO.setStockCurrentDate(LocalDate.parse(moreRecentStockUnit.getDate()));
                            stockPortfolioItemDTO.setStockCurrentPrice((float) moreRecentStockUnit.getAdjustedClose());
                            stockPortfolioItemService.save(stockPortfolioItemDTO);
                            log.debug("Updating UPDATE_STOCK_CURRENT : {}", stockPortfolioItemDTO);
                            break;
                        case UPDATE_STOCK_CURRENT_CURRENCY:
                            ForexResponse forexResponse = AlphaVantage
                                .api()
                                .forex()
                                .daily()
                                .fromSymbol("USD")
                                .toSymbol("EUR")
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
                            stockPortfolioItemService.save(stockPortfolioItemDTO);
                            log.debug("Updating UPDATE_STOCK_CURRENT_CURRENCY : {}", stockPortfolioItemDTO);
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
