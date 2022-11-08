package org.mgoulene.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;
import org.mgoulene.domain.enumeration.Currency;

/**
 * A DTO for the {@link org.mgoulene.domain.StockPortfolioItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockPortfolioItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 2, max = 10)
    private String stockSymbol;

    @NotNull
    private Currency stockCurrency;

    @NotNull
    private LocalDate stockAcquisitionDate;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockSharesNumber;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockAcquisitionPrice;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockCurrentPrice;

    @NotNull
    private LocalDate stockCurrentDate;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockAcquisitionCurrencyFactor;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockCurrentCurrencyFactor;

    @NotNull
    @DecimalMin(value = "0")
    private Float stockPriceAtAcquisitionDate;

    private BankAccountDTO bankAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public Currency getStockCurrency() {
        return stockCurrency;
    }

    public void setStockCurrency(Currency stockCurrency) {
        this.stockCurrency = stockCurrency;
    }

    public LocalDate getStockAcquisitionDate() {
        return stockAcquisitionDate;
    }

    public void setStockAcquisitionDate(LocalDate stockAcquisitionDate) {
        this.stockAcquisitionDate = stockAcquisitionDate;
    }

    public Float getStockSharesNumber() {
        return stockSharesNumber;
    }

    public void setStockSharesNumber(Float stockSharesNumber) {
        this.stockSharesNumber = stockSharesNumber;
    }

    public Float getStockAcquisitionPrice() {
        return stockAcquisitionPrice;
    }

    public void setStockAcquisitionPrice(Float stockAcquisitionPrice) {
        this.stockAcquisitionPrice = stockAcquisitionPrice;
    }

    public Float getStockCurrentPrice() {
        return stockCurrentPrice;
    }

    public void setStockCurrentPrice(Float stockCurrentPrice) {
        this.stockCurrentPrice = stockCurrentPrice;
    }

    public LocalDate getStockCurrentDate() {
        return stockCurrentDate;
    }

    public void setStockCurrentDate(LocalDate stockCurrentDate) {
        this.stockCurrentDate = stockCurrentDate;
    }

    public Float getStockAcquisitionCurrencyFactor() {
        return stockAcquisitionCurrencyFactor;
    }

    public void setStockAcquisitionCurrencyFactor(Float stockAcquisitionCurrencyFactor) {
        this.stockAcquisitionCurrencyFactor = stockAcquisitionCurrencyFactor;
    }

    public Float getStockCurrentCurrencyFactor() {
        return stockCurrentCurrencyFactor;
    }

    public void setStockCurrentCurrencyFactor(Float stockCurrentCurrencyFactor) {
        this.stockCurrentCurrencyFactor = stockCurrentCurrencyFactor;
    }

    public Float getStockPriceAtAcquisitionDate() {
        return stockPriceAtAcquisitionDate;
    }

    public void setStockPriceAtAcquisitionDate(Float stockPriceAtAcquisitionDate) {
        this.stockPriceAtAcquisitionDate = stockPriceAtAcquisitionDate;
    }

    public BankAccountDTO getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccountDTO bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockPortfolioItemDTO)) {
            return false;
        }

        StockPortfolioItemDTO stockPortfolioItemDTO = (StockPortfolioItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockPortfolioItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockPortfolioItemDTO{" +
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
            ", stockPriceAtAcquisitionDate=" + getStockPriceAtAcquisitionDate() +
            ", bankAccount=" + getBankAccount() +
            "}";
    }
}
