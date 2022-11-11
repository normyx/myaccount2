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
import java.time.LocalDate;
import java.util.List;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import org.mgoulene.domain.enumeration.Currency;
import org.mgoulene.service.StockMarketDataQueryService;
import org.mgoulene.service.StockMarketDataService;
import org.mgoulene.service.criteria.StockMarketDataCriteria;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.StringFilter;

@Service
public class MyaAlphaVantageActivityStackService {

    private final Logger log = LoggerFactory.getLogger(MyaAlphaVantageActivityStackService.class);
    private ActivityTimerTask activityTimer;
    private Timer timer;

    @Value("${alphavantage.loop.every}")
    private int loopEvery;

    @Value("${alphavantage.loop.iteration}")
    private int iteration;

    @Value("${alphavantage.key}")
    private String key;

    private final StockMarketDataQueryService marketDataQueryService;
    private final StockMarketDataService marketDataService;

    public MyaAlphaVantageActivityStackService(
        StockMarketDataQueryService marketDataQueryService,
        StockMarketDataService marketDataService
    ) {
        this.marketDataQueryService = marketDataQueryService;
        this.marketDataService = marketDataService;
    }

    @PostConstruct
    private void init() {
        log.debug("Initializing MyaAlphaVantageActivityStackService");
        activityTimer = new ActivityTimerTask(marketDataQueryService, marketDataService, key, iteration);
        timer = new Timer(true);
        timer.scheduleAtFixedRate(activityTimer, 0, loopEvery);
    }

    public void add(ActivityType type, String symbol) {
        activityTimer.add(type, symbol);
    }

    public enum ActivityType {
        UPDATE_STOCK_PRICES,
        UPDATE_STOCK_CURRENCIES,
        UPDATE_CRYPTO,
    }

    private class ActivityTimerTask extends TimerTask {

        private final Logger log = LoggerFactory.getLogger(ActivityTimerTask.class);
        private Stack<Activity> activities = new Stack<>();
        private final StockMarketDataQueryService marketDataQueryService;
        private final StockMarketDataService marketDataService;

        private int iter;

        public ActivityTimerTask(
            StockMarketDataQueryService marketDataQueryService,
            StockMarketDataService marketDataService,
            String key,
            int iteration
        ) {
            this.marketDataQueryService = marketDataQueryService;
            this.marketDataService = marketDataService;

            this.iter = iteration;
            log.debug("Initializing ActivityTimerTask with key {}", key);
            Config cfg = Config.builder().key(key).timeOut(10).build();
            AlphaVantage.api().init(cfg);
        }

        public void add(ActivityType type, String symbol) {
            activities.push(new Activity(type, symbol));
        }

        private void updateStockMarketData(String symbolKey, LocalDate date, Float value) {
            StockMarketDataCriteria criteria = new StockMarketDataCriteria();
            StringFilter symbolFilter = new StringFilter();

            symbolFilter.setEquals(symbolKey);
            criteria.setSymbol(symbolFilter);
            LocalDateFilter dateFilter = new LocalDateFilter();
            dateFilter.setEquals(date);
            criteria.setDataDate(dateFilter);
            List<StockMarketDataDTO> stockData = marketDataQueryService.findByCriteria(criteria);
            if (stockData.size() == 0) {
                StockMarketDataDTO dto = new StockMarketDataDTO();
                dto.setCloseValue(value);
                dto.setDataDate(date);
                dto.setSymbol(symbolKey);
                marketDataService.save(dto);
            } else if (stockData.size() > 1) {
                log.error("More than one data for Symbol {} at date {}", symbolKey, date);
            }
        }

        private void updateCrypto(String symbol) {
            CryptoResponse cryptoResponse = AlphaVantage.api().crypto().daily().market(Currency.EUR.name()).forSymbol(symbol).fetchSync();
            for (CryptoUnit cu : cryptoResponse.getCryptoUnits()) {
                String symbolKey = symbol + Currency.EUR.name();
                LocalDate date = LocalDate.parse(cu.getDate());
                updateStockMarketData(symbolKey, date, (float) cu.getClose());
            }
        }

        private void updateStockPrices(String symbol) {
            TimeSeriesResponse response = AlphaVantage
                .api()
                .timeSeries()
                .daily()
                .adjusted()
                .forSymbol(symbol)
                .outputSize(OutputSize.FULL)
                .fetchSync();
            for (StockUnit su : response.getStockUnits()) {
                String symbolKey = symbol;
                LocalDate date = LocalDate.parse(su.getDate());
                updateStockMarketData(symbolKey, date, (float) su.getAdjustedClose());
            }
        }

        private void updateStockCurrencies(String symbol) {
            ForexResponse forexResponse = AlphaVantage
                .api()
                .forex()
                .daily()
                .fromSymbol(symbol)
                .toSymbol(Currency.EUR.name())
                .outputSize(OutputSize.FULL)
                .fetchSync();
            for (ForexUnit fu : forexResponse.getForexUnits()) {
                String symbolKey = symbol + Currency.EUR.name();
                LocalDate date = LocalDate.parse(fu.getDate());
                updateStockMarketData(symbolKey, date, (float) fu.getClose());
            }
        }

        @Override
        public void run() {
            for (int i = 0; i < iter; i++) {
                if (!activities.empty()) {
                    Activity activity = activities.pop();
                    String symbol = activity.getSymbol();
                    switch (activity.getActivityType()) {
                        case UPDATE_STOCK_PRICES:
                            updateStockPrices(symbol);
                            break;
                        case UPDATE_STOCK_CURRENCIES:
                            updateStockCurrencies(symbol);
                            break;
                        case UPDATE_CRYPTO:
                            updateCrypto(symbol);
                            break;
                    }
                }
            }
        }
    }

    private class Activity {

        private ActivityType activityType;
        private String symbol;

        public Activity(ActivityType activityType, String symbol) {
            this.activityType = activityType;
            this.symbol = symbol;
        }

        public ActivityType getActivityType() {
            return activityType;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
