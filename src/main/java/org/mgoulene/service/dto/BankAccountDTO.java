package org.mgoulene.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;
import org.mgoulene.domain.enumeration.BankAccountType;

/**
 * A DTO for the {@link org.mgoulene.domain.BankAccount} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BankAccountDTO implements Serializable {

    private Long id;

    @NotNull
    private String accountName;

    @NotNull
    private String accountBank;

    @NotNull
    private Float initialAmount;

    @NotNull
    private Boolean archived;

    @Size(max = 40)
    private String shortName;

    @NotNull
    private BankAccountType accountType;

    @NotNull
    private Float adjustmentAmount;

    private ApplicationUserDTO account;

    private StockPortfolioItemDTO stockPortfolioItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public Float getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(Float initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public BankAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(BankAccountType accountType) {
        this.accountType = accountType;
    }

    public Float getAdjustmentAmount() {
        return adjustmentAmount;
    }

    public void setAdjustmentAmount(Float adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    public ApplicationUserDTO getAccount() {
        return account;
    }

    public void setAccount(ApplicationUserDTO account) {
        this.account = account;
    }

    public StockPortfolioItemDTO getStockPortfolioItem() {
        return stockPortfolioItem;
    }

    public void setStockPortfolioItem(StockPortfolioItemDTO stockPortfolioItem) {
        this.stockPortfolioItem = stockPortfolioItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccountDTO)) {
            return false;
        }

        BankAccountDTO bankAccountDTO = (BankAccountDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bankAccountDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BankAccountDTO{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", accountBank='" + getAccountBank() + "'" +
            ", initialAmount=" + getInitialAmount() +
            ", archived='" + getArchived() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", accountType='" + getAccountType() + "'" +
            ", adjustmentAmount=" + getAdjustmentAmount() +
            ", account=" + getAccount() +
            ", stockPortfolioItem=" + getStockPortfolioItem() +
            "}";
    }
}
