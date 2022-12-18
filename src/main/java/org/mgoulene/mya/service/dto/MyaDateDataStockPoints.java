package org.mgoulene.mya.service.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.domain.enumeration.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataStockPoints extends MyaDateDataPoints<MyaDateDataPointsStockProperties, MyaDateDataStockPointData> {

    private final Logger log = LoggerFactory.getLogger(MyaDateDataStockPoints.class);

    public MyaDateDataStockPoints(
        List<StockMarketData> stockDatas,
        String symbol,
        Currency currency,
        float numberOfShare,
        LocalDate acquisitionDate,
        float acquisitionPrice,
        MyaStockDataList currencyDatas
    ) {
        super(new MyaDateDataPointsStockProperties(symbol, currency));
        // First Filter stock data from acquisitionDate
        List<StockMarketData> filteredStockMarketData = stockDatas
            .stream()
            .filter(stockMarketData ->
                stockMarketData.getDataDate().isAfter(acquisitionDate) || stockMarketData.getDataDate().isEqual(acquisitionDate)
            )
            .collect(Collectors.toList());
        StockMarketData initialCurrencyData = null;
        if (currency != null && currency != Currency.EUR) {
            Optional<StockMarketData> initialCurrencyDataOpt = currencyDatas.getFromDate(acquisitionDate);
            if (initialCurrencyDataOpt.isPresent()) {
                initialCurrencyData = initialCurrencyDataOpt.get();
            }
        }
        for (StockMarketData stockData : filteredStockMarketData) {
            StockMarketData currentCurrencyData = null;
            if (currency != null && currency != Currency.EUR) {
                Optional<StockMarketData> currentCurrencyDataOpt = currencyDatas.getFromDate(stockData.getDataDate());
                if (currentCurrencyDataOpt.isPresent()) {
                    currentCurrencyData = currentCurrencyDataOpt.get();
                }
            }
            addPoint(
                new MyaDateDataPoint<>(
                    stockData.getDataDate(),
                    new MyaDateDataStockPointData(
                        this.getProperties(),
                        stockData,
                        numberOfShare,
                        acquisitionPrice,
                        currentCurrencyData,
                        initialCurrencyData
                    )
                )
            );
        }
    }

    public MyaDateDataSinglePoints toSimplePoints() {
        MyaDateDataSinglePoints singlePoints = new MyaDateDataSinglePoints(null);
        for (MyaDateDataPoint<MyaDateDataStockPointData> point : this.getPoints()) {
            singlePoints.addPoint(
                new MyaDateDataPoint<MyaDateDataSinglePointData>(
                    point.getDate(),
                    new MyaDateDataSinglePointData(point.getData().getValueInEuro())
                )
            );
        }
        return singlePoints;
    }
}
