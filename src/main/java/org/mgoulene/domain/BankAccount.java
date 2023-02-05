package org.mgoulene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.mgoulene.domain.enumeration.BankAccountType;

/**
 * A BankAccount.
 */
@Entity
@Table(name = "bank_account")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "bankaccount")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "account_name", nullable = false)
    private String accountName;

    @NotNull
    @Column(name = "account_bank", nullable = false)
    private String accountBank;

    @NotNull
    @Column(name = "initial_amount", nullable = false)
    private Float initialAmount;

    @NotNull
    @Column(name = "archived", nullable = false)
    private Boolean archived;

    @Size(max = 40)
    @Column(name = "short_name", length = 40)
    private String shortName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private BankAccountType accountType;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private ApplicationUser account;

    @OneToMany(mappedBy = "bankAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "bankAccount" }, allowSetters = true)
    private Set<StockPortfolioItem> stockPortfolioItems = new HashSet<>();

    @OneToMany(mappedBy = "bankAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "bankAccount" }, allowSetters = true)
    private Set<RealEstateItem> realEstateItems = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BankAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountName() {
        return this.accountName;
    }

    public BankAccount accountName(String accountName) {
        this.setAccountName(accountName);
        return this;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountBank() {
        return this.accountBank;
    }

    public BankAccount accountBank(String accountBank) {
        this.setAccountBank(accountBank);
        return this;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public Float getInitialAmount() {
        return this.initialAmount;
    }

    public BankAccount initialAmount(Float initialAmount) {
        this.setInitialAmount(initialAmount);
        return this;
    }

    public void setInitialAmount(Float initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Boolean getArchived() {
        return this.archived;
    }

    public BankAccount archived(Boolean archived) {
        this.setArchived(archived);
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getShortName() {
        return this.shortName;
    }

    public BankAccount shortName(String shortName) {
        this.setShortName(shortName);
        return this;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public BankAccountType getAccountType() {
        return this.accountType;
    }

    public BankAccount accountType(BankAccountType accountType) {
        this.setAccountType(accountType);
        return this;
    }

    public void setAccountType(BankAccountType accountType) {
        this.accountType = accountType;
    }

    public ApplicationUser getAccount() {
        return this.account;
    }

    public void setAccount(ApplicationUser applicationUser) {
        this.account = applicationUser;
    }

    public BankAccount account(ApplicationUser applicationUser) {
        this.setAccount(applicationUser);
        return this;
    }

    public Set<StockPortfolioItem> getStockPortfolioItems() {
        return this.stockPortfolioItems;
    }

    public void setStockPortfolioItems(Set<StockPortfolioItem> stockPortfolioItems) {
        if (this.stockPortfolioItems != null) {
            this.stockPortfolioItems.forEach(i -> i.setBankAccount(null));
        }
        if (stockPortfolioItems != null) {
            stockPortfolioItems.forEach(i -> i.setBankAccount(this));
        }
        this.stockPortfolioItems = stockPortfolioItems;
    }

    public BankAccount stockPortfolioItems(Set<StockPortfolioItem> stockPortfolioItems) {
        this.setStockPortfolioItems(stockPortfolioItems);
        return this;
    }

    public BankAccount addStockPortfolioItem(StockPortfolioItem stockPortfolioItem) {
        this.stockPortfolioItems.add(stockPortfolioItem);
        stockPortfolioItem.setBankAccount(this);
        return this;
    }

    public BankAccount removeStockPortfolioItem(StockPortfolioItem stockPortfolioItem) {
        this.stockPortfolioItems.remove(stockPortfolioItem);
        stockPortfolioItem.setBankAccount(null);
        return this;
    }

    public Set<RealEstateItem> getRealEstateItems() {
        return this.realEstateItems;
    }

    public void setRealEstateItems(Set<RealEstateItem> realEstateItems) {
        if (this.realEstateItems != null) {
            this.realEstateItems.forEach(i -> i.setBankAccount(null));
        }
        if (realEstateItems != null) {
            realEstateItems.forEach(i -> i.setBankAccount(this));
        }
        this.realEstateItems = realEstateItems;
    }

    public BankAccount realEstateItems(Set<RealEstateItem> realEstateItems) {
        this.setRealEstateItems(realEstateItems);
        return this;
    }

    public BankAccount addRealEstateItem(RealEstateItem realEstateItem) {
        this.realEstateItems.add(realEstateItem);
        realEstateItem.setBankAccount(this);
        return this;
    }

    public BankAccount removeRealEstateItem(RealEstateItem realEstateItem) {
        this.realEstateItems.remove(realEstateItem);
        realEstateItem.setBankAccount(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccount)) {
            return false;
        }
        return id != null && id.equals(((BankAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + getId() +
            ", accountName='" + getAccountName() + "'" +
            ", accountBank='" + getAccountBank() + "'" +
            ", initialAmount=" + getInitialAmount() +
            ", archived='" + getArchived() + "'" +
            ", shortName='" + getShortName() + "'" +
            ", accountType='" + getAccountType() + "'" +
            "}";
    }
}
