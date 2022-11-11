package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.StockMarketData} entity. This class is used
 * in {@link org.mgoulene.web.rest.StockMarketDataResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-market-data?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockMarketDataCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter symbol;

    private LocalDateFilter dataDate;

    private FloatFilter closeValue;

    private Boolean distinct;

    public StockMarketDataCriteria() {}

    public StockMarketDataCriteria(StockMarketDataCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.symbol = other.symbol == null ? null : other.symbol.copy();
        this.dataDate = other.dataDate == null ? null : other.dataDate.copy();
        this.closeValue = other.closeValue == null ? null : other.closeValue.copy();
        this.distinct = other.distinct;
    }

    @Override
    public StockMarketDataCriteria copy() {
        return new StockMarketDataCriteria(this);
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

    public StringFilter getSymbol() {
        return symbol;
    }

    public StringFilter symbol() {
        if (symbol == null) {
            symbol = new StringFilter();
        }
        return symbol;
    }

    public void setSymbol(StringFilter symbol) {
        this.symbol = symbol;
    }

    public LocalDateFilter getDataDate() {
        return dataDate;
    }

    public LocalDateFilter dataDate() {
        if (dataDate == null) {
            dataDate = new LocalDateFilter();
        }
        return dataDate;
    }

    public void setDataDate(LocalDateFilter dataDate) {
        this.dataDate = dataDate;
    }

    public FloatFilter getCloseValue() {
        return closeValue;
    }

    public FloatFilter closeValue() {
        if (closeValue == null) {
            closeValue = new FloatFilter();
        }
        return closeValue;
    }

    public void setCloseValue(FloatFilter closeValue) {
        this.closeValue = closeValue;
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
        final StockMarketDataCriteria that = (StockMarketDataCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(symbol, that.symbol) &&
            Objects.equals(dataDate, that.dataDate) &&
            Objects.equals(closeValue, that.closeValue) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, symbol, dataDate, closeValue, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockMarketDataCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (symbol != null ? "symbol=" + symbol + ", " : "") +
            (dataDate != null ? "dataDate=" + dataDate + ", " : "") +
            (closeValue != null ? "closeValue=" + closeValue + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
