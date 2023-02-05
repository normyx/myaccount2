package org.mgoulene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RealEstateItem.
 */
@Entity
@Table(name = "real_estate_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "realestateitem")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RealEstateItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "loan_value", nullable = false)
    private Float loanValue;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_value", nullable = false)
    private Float totalValue;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "percent_owned", nullable = false)
    private Float percentOwned;

    @NotNull
    @Column(name = "item_date", nullable = false)
    private LocalDate itemDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "account", "stockPortfolioItems", "realEstateItems" }, allowSetters = true)
    private BankAccount bankAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RealEstateItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getLoanValue() {
        return this.loanValue;
    }

    public RealEstateItem loanValue(Float loanValue) {
        this.setLoanValue(loanValue);
        return this;
    }

    public void setLoanValue(Float loanValue) {
        this.loanValue = loanValue;
    }

    public Float getTotalValue() {
        return this.totalValue;
    }

    public RealEstateItem totalValue(Float totalValue) {
        this.setTotalValue(totalValue);
        return this;
    }

    public void setTotalValue(Float totalValue) {
        this.totalValue = totalValue;
    }

    public Float getPercentOwned() {
        return this.percentOwned;
    }

    public RealEstateItem percentOwned(Float percentOwned) {
        this.setPercentOwned(percentOwned);
        return this;
    }

    public void setPercentOwned(Float percentOwned) {
        this.percentOwned = percentOwned;
    }

    public LocalDate getItemDate() {
        return this.itemDate;
    }

    public RealEstateItem itemDate(LocalDate itemDate) {
        this.setItemDate(itemDate);
        return this;
    }

    public void setItemDate(LocalDate itemDate) {
        this.itemDate = itemDate;
    }

    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public RealEstateItem bankAccount(BankAccount bankAccount) {
        this.setBankAccount(bankAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RealEstateItem)) {
            return false;
        }
        return id != null && id.equals(((RealEstateItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RealEstateItem{" +
            "id=" + getId() +
            ", loanValue=" + getLoanValue() +
            ", totalValue=" + getTotalValue() +
            ", percentOwned=" + getPercentOwned() +
            ", itemDate='" + getItemDate() + "'" +
            "}";
    }
}
