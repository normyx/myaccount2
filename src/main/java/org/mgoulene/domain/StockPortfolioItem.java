package org.mgoulene.domain;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.mgoulene.domain.enumeration.Currency;

/**
 * A StockPortfolioItem.
 */
@Entity
@Table(name = "stock_portfolio_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "stockportfolioitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockPortfolioItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 10)
    @Column(name = "stock_symbol", length = 10, nullable = false)
    private String stockSymbol;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_currency", nullable = false)
    private Currency stockCurrency;

    @NotNull
    @Column(name = "stock_acquisition_date", nullable = false)
    private LocalDate stockAcquisitionDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "stock_shares_number", nullable = false)
    private Float stockSharesNumber;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "stock_acquisition_price", nullable = false)
    private Float stockAcquisitionPrice;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "stock_current_price", nullable = false)
    private Float stockCurrentPrice;

    @NotNull
    @Column(name = "stock_current_date", nullable = false)
    private LocalDate stockCurrentDate;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "stock_acquisition_currency_factor", nullable = false)
    private Float stockAcquisitionCurrencyFactor;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "stock_current_currency_factor", nullable = false)
    private Float stockCurrentCurrencyFactor;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockPortfolioItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockSymbol() {
        return this.stockSymbol;
    }

    public StockPortfolioItem stockSymbol(String stockSymbol) {
        this.setStockSymbol(stockSymbol);
        return this;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public Currency getStockCurrency() {
        return this.stockCurrency;
    }

    public StockPortfolioItem stockCurrency(Currency stockCurrency) {
        this.setStockCurrency(stockCurrency);
        return this;
    }

    public void setStockCurrency(Currency stockCurrency) {
        this.stockCurrency = stockCurrency;
    }

    public LocalDate getStockAcquisitionDate() {
        return this.stockAcquisitionDate;
    }

    public StockPortfolioItem stockAcquisitionDate(LocalDate stockAcquisitionDate) {
        this.setStockAcquisitionDate(stockAcquisitionDate);
        return this;
    }

    public void setStockAcquisitionDate(LocalDate stockAcquisitionDate) {
        this.stockAcquisitionDate = stockAcquisitionDate;
    }

    public Float getStockSharesNumber() {
        return this.stockSharesNumber;
    }

    public StockPortfolioItem stockSharesNumber(Float stockSharesNumber) {
        this.setStockSharesNumber(stockSharesNumber);
        return this;
    }

    public void setStockSharesNumber(Float stockSharesNumber) {
        this.stockSharesNumber = stockSharesNumber;
    }

    public Float getStockAcquisitionPrice() {
        return this.stockAcquisitionPrice;
    }

    public StockPortfolioItem stockAcquisitionPrice(Float stockAcquisitionPrice) {
        this.setStockAcquisitionPrice(stockAcquisitionPrice);
        return this;
    }

    public void setStockAcquisitionPrice(Float stockAcquisitionPrice) {
        this.stockAcquisitionPrice = stockAcquisitionPrice;
    }

    public Float getStockCurrentPrice() {
        return this.stockCurrentPrice;
    }

    public StockPortfolioItem stockCurrentPrice(Float stockCurrentPrice) {
        this.setStockCurrentPrice(stockCurrentPrice);
        return this;
    }

    public void setStockCurrentPrice(Float stockCurrentPrice) {
        this.stockCurrentPrice = stockCurrentPrice;
    }

    public LocalDate getStockCurrentDate() {
        return this.stockCurrentDate;
    }

    public StockPortfolioItem stockCurrentDate(LocalDate stockCurrentDate) {
        this.setStockCurrentDate(stockCurrentDate);
        return this;
    }

    public void setStockCurrentDate(LocalDate stockCurrentDate) {
        this.stockCurrentDate = stockCurrentDate;
    }

    public Float getStockAcquisitionCurrencyFactor() {
        return this.stockAcquisitionCurrencyFactor;
    }

    public StockPortfolioItem stockAcquisitionCurrencyFactor(Float stockAcquisitionCurrencyFactor) {
        this.setStockAcquisitionCurrencyFactor(stockAcquisitionCurrencyFactor);
        return this;
    }

    public void setStockAcquisitionCurrencyFactor(Float stockAcquisitionCurrencyFactor) {
        this.stockAcquisitionCurrencyFactor = stockAcquisitionCurrencyFactor;
    }

    public Float getStockCurrentCurrencyFactor() {
        return this.stockCurrentCurrencyFactor;
    }

    public StockPortfolioItem stockCurrentCurrencyFactor(Float stockCurrentCurrencyFactor) {
        this.setStockCurrentCurrencyFactor(stockCurrentCurrencyFactor);
        return this;
    }

    public void setStockCurrentCurrencyFactor(Float stockCurrentCurrencyFactor) {
        this.stockCurrentCurrencyFactor = stockCurrentCurrencyFactor;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockPortfolioItem)) {
            return false;
        }
        return id != null && id.equals(((StockPortfolioItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockPortfolioItem{" +
            "id=" + getId() +
            ", stockSymbol='" + getStockSymbol() + "'" +
            ", stockCurrency='" + getStockCurrency() + "'" +
            ", stockAcquisitionDate='" + getStockAcquisitionDate() + "'" +
            ", stockSharesNumber=" + getStockSharesNumber() +
            ", stockAcquisitionPrice=" + getStockAcquisitionPrice() +
            ", stockCurrentPrice=" + getStockCurrentPrice() +
            ", stockCurrentDate='" + getStockCurrentDate() + "'" +
            ", stockAcquisitionCurrencyFactor=" + getStockAcquisitionCurrencyFactor() +
            ", stockCurrentCurrencyFactor=" + getStockCurrentCurrencyFactor() +
            "}";
    }
}
