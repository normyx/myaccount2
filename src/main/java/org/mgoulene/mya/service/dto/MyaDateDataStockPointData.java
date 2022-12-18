package org.mgoulene.mya.service.dto;

import org.mgoulene.domain.StockMarketData;
import org.mgoulene.domain.enumeration.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataStockPointData implements MyaDateDataPointData {

    private final Logger log = LoggerFactory.getLogger(MyaDateDataStockPointData.class);
    private float valueInCurrency;
    private float valueInEuro;
    private float initialValueInCurrency;
    private float initialValueInEuro;
    private float indexInCurrency;
    private float indexInEuro;
    private MyaDateDataPointsStockProperties props;

    public MyaDateDataStockPointData(MyaDateDataPointsStockProperties props) {
        this.props = props;
        this.valueInEuro = 0;
        this.valueInCurrency = 0;
        this.initialValueInCurrency = 0;
        this.initialValueInEuro = 0;
        this.indexInCurrency = 1;
        this.indexInEuro = 1;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public MyaDateDataStockPointData(
        MyaDateDataPointsStockProperties props,
        StockMarketData currentStockData,
        float numberOfStock,
        float initialStockPrice,
        StockMarketData currentCurrencyData,
        StockMarketData initialCurrencyData
    ) {
        this.props = props;
        this.valueInCurrency = numberOfStock * currentStockData.getCloseValue();
        this.initialValueInCurrency = numberOfStock * initialStockPrice;
        if (props.getCurrency() != Currency.EUR) {
            this.valueInEuro = this.valueInCurrency * currentCurrencyData.getCloseValue();
            this.initialValueInEuro = this.initialValueInCurrency * initialCurrencyData.getCloseValue();
        } else {
            this.valueInEuro = this.valueInCurrency;
            this.initialValueInEuro = this.initialValueInCurrency;
        }
        this.consolidate();
    }

    public float getValueInCurrency() {
        return valueInCurrency;
    }

    public float getValueInEuro() {
        return valueInEuro;
    }

    public float getInitialValueInCurrency() {
        return initialValueInCurrency;
    }

    public float getInitialValueInEuro() {
        return initialValueInEuro;
    }

    public float getIndexInCurrency() {
        return indexInCurrency;
    }

    public float getIndexInEuro() {
        return indexInEuro;
    }

    public MyaDateDataPointsStockProperties getProps() {
        return props;
    }

    @Override
    public void add(MyaDateDataPointData other) {
        if (other != null) {
            MyaDateDataStockPointData myaDateDataStockPointData = (MyaDateDataStockPointData) other;

            this.props = (MyaDateDataPointsStockProperties) this.props.merge(myaDateDataStockPointData.getProps());
            this.valueInCurrency += myaDateDataStockPointData.getValueInCurrency();
            this.initialValueInCurrency += myaDateDataStockPointData.getInitialValueInCurrency();
            this.valueInEuro += myaDateDataStockPointData.getValueInEuro();
            this.initialValueInEuro += myaDateDataStockPointData.getInitialValueInEuro();
            this.consolidate();
        }
    }

    @Override
    public void consolidate() {
        this.indexInCurrency = this.valueInCurrency / this.initialValueInCurrency;
        this.indexInEuro = this.valueInEuro / this.initialValueInEuro;
    }

    @Override
    public String toString() {
        return (
            "MyaDateDataStockPointData [valueInCurrency=" +
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
            ", props=" +
            props +
            "]"
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(valueInCurrency);
        result = prime * result + Float.floatToIntBits(valueInEuro);
        result = prime * result + Float.floatToIntBits(initialValueInCurrency);
        result = prime * result + Float.floatToIntBits(initialValueInEuro);
        result = prime * result + Float.floatToIntBits(indexInCurrency);
        result = prime * result + Float.floatToIntBits(indexInEuro);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MyaDateDataStockPointData other = (MyaDateDataStockPointData) obj;
        if (Float.floatToIntBits(valueInCurrency) != Float.floatToIntBits(other.valueInCurrency)) return false;
        if (Float.floatToIntBits(valueInEuro) != Float.floatToIntBits(other.valueInEuro)) return false;
        if (Float.floatToIntBits(initialValueInCurrency) != Float.floatToIntBits(other.initialValueInCurrency)) return false;
        if (Float.floatToIntBits(initialValueInEuro) != Float.floatToIntBits(other.initialValueInEuro)) return false;
        if (Float.floatToIntBits(indexInCurrency) != Float.floatToIntBits(other.indexInCurrency)) return false;
        if (Float.floatToIntBits(indexInEuro) != Float.floatToIntBits(other.indexInEuro)) return false;
        return true;
    }
}
