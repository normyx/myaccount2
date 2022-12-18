package org.mgoulene.mya.service.dto;

import org.mgoulene.domain.enumeration.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataPointsStockProperties extends MyaDateDataPointsProperties {

    private final Logger log = LoggerFactory.getLogger(MyaDateDataStockPoints.class);
    private String symbol;
    private Currency currency;

    public MyaDateDataPointsStockProperties(String symbol, Currency currency) {
        this.symbol = symbol;
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "MyaDateDataPointsStockProperties [symbol=" + symbol + ", currency=" + currency + "]";
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public MyaDateDataPointsProperties merge(MyaDateDataPointsProperties otherProps) {
        if (otherProps instanceof MyaDateDataPointsStockProperties) {
            String s = symbol == null ? null : symbol.equals(((MyaDateDataPointsStockProperties) otherProps).getSymbol()) ? symbol : null;
            Currency c = currency == null
                ? null
                : currency.equals(((MyaDateDataPointsStockProperties) otherProps).getCurrency()) ? currency : null;
            MyaDateDataPointsStockProperties returnProps = new MyaDateDataPointsStockProperties(s, c);

            return returnProps;
        }

        return null;
    }
}
