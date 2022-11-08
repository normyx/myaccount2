package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.mgoulene.domain.enumeration.Currency;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.StockPortfolioItem} entity. This class is used
 * in {@link org.mgoulene.web.rest.StockPortfolioItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-portfolio-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockPortfolioItemCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Currency
     */
    public static class CurrencyFilter extends Filter<Currency> {

        public CurrencyFilter() {}

        public CurrencyFilter(CurrencyFilter filter) {
            super(filter);
        }

        @Override
        public CurrencyFilter copy() {
            return new CurrencyFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter stockSymbol;

    private CurrencyFilter stockCurrency;

    private LocalDateFilter stockAcquisitionDate;

    private FloatFilter stockSharesNumber;

    private FloatFilter stockAcquisitionPrice;

    private FloatFilter stockCurrentPrice;

    private LocalDateFilter stockCurrentDate;

    private FloatFilter stockAcquisitionCurrencyFactor;

    private FloatFilter stockCurrentCurrencyFactor;

    private Boolean distinct;

    public StockPortfolioItemCriteria() {}

    public StockPortfolioItemCriteria(StockPortfolioItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.stockSymbol = other.stockSymbol == null ? null : other.stockSymbol.copy();
        this.stockCurrency = other.stockCurrency == null ? null : other.stockCurrency.copy();
        this.stockAcquisitionDate = other.stockAcquisitionDate == null ? null : other.stockAcquisitionDate.copy();
        this.stockSharesNumber = other.stockSharesNumber == null ? null : other.stockSharesNumber.copy();
        this.stockAcquisitionPrice = other.stockAcquisitionPrice == null ? null : other.stockAcquisitionPrice.copy();
        this.stockCurrentPrice = other.stockCurrentPrice == null ? null : other.stockCurrentPrice.copy();
        this.stockCurrentDate = other.stockCurrentDate == null ? null : other.stockCurrentDate.copy();
        this.stockAcquisitionCurrencyFactor =
            other.stockAcquisitionCurrencyFactor == null ? null : other.stockAcquisitionCurrencyFactor.copy();
        this.stockCurrentCurrencyFactor = other.stockCurrentCurrencyFactor == null ? null : other.stockCurrentCurrencyFactor.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockPortfolioItemCriteria copy() {
        return new StockPortfolioItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getStockSymbol() {
        return stockSymbol;
    }

    public StringFilter stockSymbol() {
        if (stockSymbol == null) {
            stockSymbol = new StringFilter();
        }
        return stockSymbol;
    }

    public void setStockSymbol(StringFilter stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public CurrencyFilter getStockCurrency() {
        return stockCurrency;
    }

    public CurrencyFilter stockCurrency() {
        if (stockCurrency == null) {
            stockCurrency = new CurrencyFilter();
        }
        return stockCurrency;
    }

    public void setStockCurrency(CurrencyFilter stockCurrency) {
        this.stockCurrency = stockCurrency;
    }

    public LocalDateFilter getStockAcquisitionDate() {
        return stockAcquisitionDate;
    }

    public LocalDateFilter stockAcquisitionDate() {
        if (stockAcquisitionDate == null) {
            stockAcquisitionDate = new LocalDateFilter();
        }
        return stockAcquisitionDate;
    }

    public void setStockAcquisitionDate(LocalDateFilter stockAcquisitionDate) {
        this.stockAcquisitionDate = stockAcquisitionDate;
    }

    public FloatFilter getStockSharesNumber() {
        return stockSharesNumber;
    }

    public FloatFilter stockSharesNumber() {
        if (stockSharesNumber == null) {
            stockSharesNumber = new FloatFilter();
        }
        return stockSharesNumber;
    }

    public void setStockSharesNumber(FloatFilter stockSharesNumber) {
        this.stockSharesNumber = stockSharesNumber;
    }

    public FloatFilter getStockAcquisitionPrice() {
        return stockAcquisitionPrice;
    }

    public FloatFilter stockAcquisitionPrice() {
        if (stockAcquisitionPrice == null) {
            stockAcquisitionPrice = new FloatFilter();
        }
        return stockAcquisitionPrice;
    }

    public void setStockAcquisitionPrice(FloatFilter stockAcquisitionPrice) {
        this.stockAcquisitionPrice = stockAcquisitionPrice;
    }

    public FloatFilter getStockCurrentPrice() {
        return stockCurrentPrice;
    }

    public FloatFilter stockCurrentPrice() {
        if (stockCurrentPrice == null) {
            stockCurrentPrice = new FloatFilter();
        }
        return stockCurrentPrice;
    }

    public void setStockCurrentPrice(FloatFilter stockCurrentPrice) {
        this.stockCurrentPrice = stockCurrentPrice;
    }

    public LocalDateFilter getStockCurrentDate() {
        return stockCurrentDate;
    }

    public LocalDateFilter stockCurrentDate() {
        if (stockCurrentDate == null) {
            stockCurrentDate = new LocalDateFilter();
        }
        return stockCurrentDate;
    }

    public void setStockCurrentDate(LocalDateFilter stockCurrentDate) {
        this.stockCurrentDate = stockCurrentDate;
    }

    public FloatFilter getStockAcquisitionCurrencyFactor() {
        return stockAcquisitionCurrencyFactor;
    }

    public FloatFilter stockAcquisitionCurrencyFactor() {
        if (stockAcquisitionCurrencyFactor == null) {
            stockAcquisitionCurrencyFactor = new FloatFilter();
        }
        return stockAcquisitionCurrencyFactor;
    }

    public void setStockAcquisitionCurrencyFactor(FloatFilter stockAcquisitionCurrencyFactor) {
        this.stockAcquisitionCurrencyFactor = stockAcquisitionCurrencyFactor;
    }

    public FloatFilter getStockCurrentCurrencyFactor() {
        return stockCurrentCurrencyFactor;
    }

    public FloatFilter stockCurrentCurrencyFactor() {
        if (stockCurrentCurrencyFactor == null) {
            stockCurrentCurrencyFactor = new FloatFilter();
        }
        return stockCurrentCurrencyFactor;
    }

    public void setStockCurrentCurrencyFactor(FloatFilter stockCurrentCurrencyFactor) {
        this.stockCurrentCurrencyFactor = stockCurrentCurrencyFactor;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StockPortfolioItemCriteria that = (StockPortfolioItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(stockSymbol, that.stockSymbol) &&
            Objects.equals(stockCurrency, that.stockCurrency) &&
            Objects.equals(stockAcquisitionDate, that.stockAcquisitionDate) &&
            Objects.equals(stockSharesNumber, that.stockSharesNumber) &&
            Objects.equals(stockAcquisitionPrice, that.stockAcquisitionPrice) &&
            Objects.equals(stockCurrentPrice, that.stockCurrentPrice) &&
            Objects.equals(stockCurrentDate, that.stockCurrentDate) &&
            Objects.equals(stockAcquisitionCurrencyFactor, that.stockAcquisitionCurrencyFactor) &&
            Objects.equals(stockCurrentCurrencyFactor, that.stockCurrentCurrencyFactor) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            stockSymbol,
            stockCurrency,
            stockAcquisitionDate,
            stockSharesNumber,
            stockAcquisitionPrice,
            stockCurrentPrice,
            stockCurrentDate,
            stockAcquisitionCurrencyFactor,
            stockCurrentCurrencyFactor,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockPortfolioItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (stockSymbol != null ? "stockSymbol=" + stockSymbol + ", " : "") +
            (stockCurrency != null ? "stockCurrency=" + stockCurrency + ", " : "") +
            (stockAcquisitionDate != null ? "stockAcquisitionDate=" + stockAcquisitionDate + ", " : "") +
            (stockSharesNumber != null ? "stockSharesNumber=" + stockSharesNumber + ", " : "") +
            (stockAcquisitionPrice != null ? "stockAcquisitionPrice=" + stockAcquisitionPrice + ", " : "") +
            (stockCurrentPrice != null ? "stockCurrentPrice=" + stockCurrentPrice + ", " : "") +
            (stockCurrentDate != null ? "stockCurrentDate=" + stockCurrentDate + ", " : "") +
            (stockAcquisitionCurrencyFactor != null ? "stockAcquisitionCurrencyFactor=" + stockAcquisitionCurrencyFactor + ", " : "") +
            (stockCurrentCurrencyFactor != null ? "stockCurrentCurrencyFactor=" + stockCurrentCurrencyFactor + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
