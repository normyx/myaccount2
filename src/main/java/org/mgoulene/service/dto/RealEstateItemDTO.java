package org.mgoulene.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link org.mgoulene.domain.RealEstateItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RealEstateItemDTO implements Serializable {

    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    private Float loanValue;

    @NotNull
    @DecimalMin(value = "0")
    private Float totalValue;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private Float percentOwned;

    @NotNull
    private LocalDate itemDate;

    private BankAccountDTO bankAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLoanValue() {
        return loanValue;
    }

    public void setLoanValue(Float loanValue) {
        this.loanValue = loanValue;
    }

    public Float getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Float totalValue) {
        this.totalValue = totalValue;
    }

    public Float getPercentOwned() {
        return percentOwned;
    }

    public void setPercentOwned(Float percentOwned) {
        this.percentOwned = percentOwned;
    }

    public LocalDate getItemDate() {
        return itemDate;
    }

    public void setItemDate(LocalDate itemDate) {
        this.itemDate = itemDate;
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
        if (!(o instanceof RealEstateItemDTO)) {
            return false;
        }

        RealEstateItemDTO realEstateItemDTO = (RealEstateItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, realEstateItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RealEstateItemDTO{" +
            "id=" + getId() +
            ", loanValue=" + getLoanValue() +
            ", totalValue=" + getTotalValue() +
            ", percentOwned=" + getPercentOwned() +
            ", itemDate='" + getItemDate() + "'" +
            ", bankAccount=" + getBankAccount() +
            "}";
    }
}
