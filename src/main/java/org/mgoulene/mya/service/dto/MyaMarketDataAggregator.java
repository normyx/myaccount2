package org.mgoulene.mya.service.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.domain.enumeration.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaMarketDataAggregator {

    private final Logger log = LoggerFactory.getLogger(MyaMarketDataAggregator.class);

    private Map<String, MarketShareSymbolDatas> symbolDatasMap = new HashMap<>();

    public MyaMarketDataAggregator() {}

    public void addData(
        List<StockMarketData> data,
        List<StockMarketData> currencyData,
        String symbol,
        Currency currency,
        float shareNumber,
        float initialValueInCurrency
    ) {
        if (symbolDatasMap.get(symbol) == null) {
            this.symbolDatasMap.put(symbol, new MarketShareSymbolDatas(symbol, currency, currencyData));
        }
        symbolDatasMap.get(symbol).addData(shareNumber, initialValueInCurrency, data);
    }

    public MarketShareDataPoints computeMarketShareDataPoints(String symbol) {
        return symbolDatasMap.get(symbol).compute();
    }

    public MarketShareDataPoints computeMarketShareDataPoints() {
        Set<String> symbols = symbolDatasMap.keySet();
        MarketShareDataPoints points = null;
        for (String symbol : symbols) {
            log.debug("Processing {}", symbol);
            MarketShareDataPoints symbolPoints = symbolDatasMap.get(symbol).compute();
            if (points == null) {
                points = symbolPoints;
            } else {
                points = points.merge(symbolPoints);
            }
        }
        log.debug("Processed {}", points);
        return points;
    }

    private class MarketShareDataPoint {

        private LocalDate date;
        private float valueInCurrency;
        private float valueInEuro;
        private float initialValueInCurrency;
        private float initialValueInEuro;
        private float indexInCurrency;
        private float indexInEuro;

        public MarketShareDataPoint(LocalDate date) {
            this.date = date;
            this.initialValueInCurrency = 0;
            this.initialValueInEuro = 0;
        }

        public LocalDate getDate() {
            return date;
        }

        public float getIndexInCurrency() {
            return indexInCurrency;
        }

        public void setIndexInCurrency(float indexInCurrency) {
            this.indexInCurrency = indexInCurrency;
        }

        public float getIndexInEuro() {
            return indexInEuro;
        }

        public void setIndexInEuro(float indexInEuro) {
            this.indexInEuro = indexInEuro;
        }

        public float getInitialValueInCurrency() {
            return initialValueInCurrency;
        }

        public float getInitialValueInEuro() {
            return initialValueInEuro;
        }

        public float getValueInCurrency() {
            return valueInCurrency;
        }

        public void setValueInCurrency(float valueInCurrency) {
            this.valueInCurrency = valueInCurrency;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public void setInitialValueInCurrency(float initialValueInCurrency) {
            this.initialValueInCurrency = initialValueInCurrency;
        }

        public void setInitialValueInEuro(float initialValueInEuro) {
            this.initialValueInEuro = initialValueInEuro;
        }

        public float getValueInEuro() {
            return valueInEuro;
        }

        public void setValueInEuro(float valueInEuro) {
            this.valueInEuro = valueInEuro;
        }

        @Override
        public String toString() {
            return (
                "MarketShareDataPoint [date=" +
                date +
                ", valueInCurrency=" +
                valueInCurrency +
                ", valueInEuro=" +
                valueInEuro +
                ", initialValueInCurrency=" +
                initialValueInCurrency +
                ", initialValueInEuro=" +
                initialValueInEuro +
                ", indexInCurrency=" +
                indexInCurrency +
                ", indexInEuro=" +
                indexInEuro +
                "]"
            );
        }
    }

    public class MarketShareDataPoints {

        private String symbol;
        private Currency currency;
        private List<MarketShareDataPoint> points = new ArrayList<>();

        public MarketShareDataPoints(String symbol, Currency currency) {
            this.symbol = symbol;
            this.currency = currency;
        }

        public void addDatePoint(LocalDate date) {
            points.add(new MarketShareDataPoint(date));
        }

        public Optional<MarketShareDataPoint> get(LocalDate date) {
            for (MarketShareDataPoint point : points) {
                if (point.getDate().isEqual(date)) {
                    return Optional.of(point);
                }
            }
            return Optional.empty();
        }

        public void computeIndexes() {
            for (MarketShareDataPoint point : points) {
                point.setIndexInCurrency(point.getValueInCurrency() / point.getInitialValueInCurrency());
                point.setIndexInEuro(point.getValueInEuro() / point.getInitialValueInEuro());
            }
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(Currency currency) {
            this.currency = currency;
        }

        public List<MarketShareDataPoint> getPoints() {
            return points;
        }

        public void setPoints(List<MarketShareDataPoint> points) {
            this.points = points;
        }

        @Override
        public String toString() {
            return "MarketShareDataPoints [symbol=" + symbol + ", currency=" + currency + ", points=" + points + "]";
        }

        public MarketShareDataPoint getPointCloseToDate(LocalDate date) {
            if (points.size() != 0) {
                if (points.get(0).getDate().isAfter(date)) {
                    return null;
                }
                for (int i = 0; i < points.size(); i++) {
                    MarketShareDataPoint p = points.get(i);
                    if (date.isEqual(p.getDate())) {
                        return p;
                    } else if (date.isAfter(p.getDate()) && i != points.size() - 1 && date.isBefore(points.get(i + 1).getDate())) {
                        return p;
                    }
                }
            }
            return null;
        }

        public MarketShareDataPoints merge(MarketShareDataPoints toMerge) {
            MarketShareDataPoints returnPoints;
            String symbolA = null;
            Currency currencyA = null;
            if (this.getSymbol() != null && this.getSymbol() == toMerge.getSymbol()) {
                log.debug("SAme symbol {}", this.getSymbol());
                symbolA = this.getSymbol();
            } else {
                log.debug("Different symbol {} {}", this.getSymbol(), toMerge.getSymbol());
            }
            if (this.getCurrency() != null && this.getCurrency() == toMerge.getCurrency()) {
                log.debug("Same Currency {} {}", this.getCurrency(), toMerge.getCurrency());
                currencyA = this.getCurrency();
            }
            returnPoints = new MarketShareDataPoints(symbolA, currencyA);
            // First, get all the date
            HashMap<LocalDate, Object> dateKeys = new HashMap<>();
            for (MarketShareDataPoint p : this.points) {
                dateKeys.put(p.getDate(), null);
            }
            for (MarketShareDataPoint p : toMerge.getPoints()) {
                dateKeys.put(p.getDate(), null);
            }
            ArrayList<LocalDate> dates = new ArrayList<>(dateKeys.keySet());
            Collections.sort(dates, new LocalDateComparator());
            log.debug("Dates {} ", dates);
            for (LocalDate date : dates) {
                returnPoints.addDatePoint(date);
            }
            log.debug("AllPoints {} ", returnPoints);
            for (MarketShareDataPoint point : returnPoints.getPoints()) {
                MarketShareDataPoint pointToAddA = this.getPointCloseToDate(point.getDate());
                MarketShareDataPoint pointToAddB = toMerge.getPointCloseToDate(point.getDate());
                if (pointToAddA != null) {
                    point.initialValueInEuro += pointToAddA.initialValueInEuro;
                    point.valueInEuro += pointToAddA.valueInEuro;
                    if (returnPoints.getCurrency() != null) {
                        point.initialValueInCurrency += pointToAddA.initialValueInCurrency;
                        point.valueInCurrency += pointToAddA.valueInCurrency;
                    } else {
                        point.initialValueInCurrency += pointToAddA.initialValueInEuro;
                        point.valueInCurrency += pointToAddA.valueInEuro;
                    }
                }
                if (pointToAddB != null) {
                    point.initialValueInEuro += pointToAddB.initialValueInEuro;
                    point.valueInEuro += pointToAddB.valueInEuro;
                    if (returnPoints.getCurrency() != null) {
                        point.initialValueInCurrency += pointToAddB.initialValueInCurrency;
                        point.valueInCurrency += pointToAddB.valueInCurrency;
                    } else {
                        point.initialValueInCurrency += pointToAddB.initialValueInEuro;
                        point.valueInCurrency += pointToAddB.valueInEuro;
                    }
                }
            }

            log.debug("AllPoints processed{} ", returnPoints);
            return returnPoints;
        }
    }

    private class LocalDateComparator implements Comparator<LocalDate> {

        @Override
        public int compare(LocalDate o1, LocalDate o2) {
            return o1.compareTo(o2);
        }
    }

    private class MarketShareSymbolDatas {

        private final Logger log = LoggerFactory.getLogger(MarketShareSymbolDatas.class);
        private String symbol;
        private Currency currency;
        private MarketShareSymbolData currencyData;
        private List<MarketShareSymbolData> marketShareDatas = new ArrayList<>();

        public MarketShareSymbolDatas(String symbol, Currency currency, List<StockMarketData> currencyData) {
            this.symbol = symbol;
            this.currency = currency;

            this.currencyData = new MarketShareSymbolData(0, 0, currencyData);
        }

        void addData(float shareNumber, float initialValueInCurrency, List<StockMarketData> currencyData) {
            this.marketShareDatas.add(new MarketShareSymbolData(shareNumber, initialValueInCurrency, currencyData));
        }

        public MarketShareDataPoints compute() {
            MarketShareDataPoints data = new MarketShareDataPoints(symbol, currency);
            Set<LocalDate> dateSet = new TreeSet<>();
            // initiate all date
            for (MarketShareSymbolData symbolData : marketShareDatas) {
                for (StockMarketData marketData : symbolData.getData()) {
                    dateSet.add(marketData.getDataDate());
                }
            }
            for (LocalDate date : dateSet) {
                data.addDatePoint(date);
            }
            // log.debug("Working with dates {}", data);
            // Feed all value in currency
            for (MarketShareSymbolData marketShareSymbolData : marketShareDatas) {
                boolean isFirst = true;
                float initialValueIndex = 1;
                float index = 1;
                // log.debug("marketShareSymbolData {}", marketShareSymbolData);
                for (LocalDate date : dateSet) {
                    Optional<StockMarketData> stockMarketDataOptional = marketShareSymbolData.getStockMarketData(date);
                    // log.debug("Finding stockMarketData {}", stockMarketDataOptional.get());
                    if (stockMarketDataOptional.isPresent()) {
                        if (currency != Currency.EUR) {
                            Optional<StockMarketData> currencyOpt = currencyData.getStockMarketData(date);
                            if (currencyOpt.isPresent()) {
                                index = currencyOpt.get().getCloseValue();
                                if (isFirst) {
                                    initialValueIndex = index;
                                    isFirst = false;
                                }
                            } else {
                                throw new Error("Currency not found");
                            }
                        }
                        StockMarketData stockMarketData = stockMarketDataOptional.get();
                        Optional<MarketShareDataPoint> pointOpt = data.get(date);
                        if (pointOpt.isPresent()) {
                            MarketShareDataPoint point = pointOpt.get();
                            // log.debug("set initial data {} + {}*{} = {}",
                            // point.getInitialValueInCurrency(),
                            // marketShareSymbolData.getInitialValueInCurrency(),
                            // marketShareSymbolData.getShareNumber(), point.getInitialValueInCurrency()
                            // + marketShareSymbolData.getInitialValueInCurrency()
                            // * marketShareSymbolData.getShareNumber());
                            point.setInitialValueInCurrency(
                                point.getInitialValueInCurrency() +
                                marketShareSymbolData.getInitialValueInCurrency() *
                                marketShareSymbolData.getShareNumber()
                            );
                            point.setInitialValueInEuro(
                                point.getInitialValueInEuro() +
                                marketShareSymbolData.getInitialValueInCurrency() *
                                marketShareSymbolData.getShareNumber() *
                                initialValueIndex
                            );
                            point.setValueInCurrency(
                                point.getValueInCurrency() + stockMarketData.getCloseValue() * marketShareSymbolData.getShareNumber()
                            );
                            point.setValueInEuro(point.getValueInCurrency() * index);
                        }
                    }
                }
            }

            // Compute indexes
            data.computeIndexes();
            return data;
        }
    }

    private class MarketShareSymbolData {

        private final Logger log = LoggerFactory.getLogger(MarketShareSymbolData.class);
        private float shareNumber;
        private float initialValueInCurrency;
        private List<StockMarketData> data;

        public MarketShareSymbolData(float shareNumber, float initialValueInCurrency, List<StockMarketData> data) {
            this.shareNumber = shareNumber;
            this.initialValueInCurrency = initialValueInCurrency;
            this.data = data;
        }

        public Optional<StockMarketData> getStockMarketData(LocalDate date) {
            if (data.get(0).getDataDate().isAfter(date)) {
                return Optional.empty();
            }
            StockMarketData previous = null;
            for (StockMarketData element : data) {
                if (element.getDataDate().isEqual(date)) {
                    return Optional.of(element);
                }
                if (element.getDataDate().isAfter(date)) {
                    if (previous != null) {
                        return Optional.of(previous);
                    } else {
                        throw new Error("StockMarketData : Option Impossible");
                    }
                }
                previous = element;
            }
            throw new Error("StockMarketData not found");
        }

        public float getShareNumber() {
            return shareNumber;
        }

        public float getInitialValueInCurrency() {
            return initialValueInCurrency;
        }

        public List<StockMarketData> getData() {
            return data;
        }

        @Override
        public String toString() {
            return "MarketShareSymbolData [shareNumber=" + shareNumber + ", data=" + data + "]";
        }
    }
}
