package org.mgoulene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Operation.
 */
@Entity
@Table(name = "operation")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "operation")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 400)
    @Column(name = "jhi_label", length = 400, nullable = false)
    private String label;

    @NotNull
    @Column(name = "jhi_date", nullable = false)
    private LocalDate date;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @Size(max = 400)
    @Column(name = "note", length = 400)
    private String note;

    @Size(max = 20)
    @Column(name = "check_number", length = 20)
    private String checkNumber;

    @NotNull
    @Column(name = "is_up_to_date", nullable = false)
    private Boolean isUpToDate;

    @Column(name = "deleting_hard_lock")
    private Boolean deletingHardLock;

    @ManyToOne
    @JsonIgnoreProperties(value = { "category" }, allowSetters = true)
    private SubCategory subCategory;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private ApplicationUser account;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "account", "stockPortfolioItem" }, allowSetters = true)
    private BankAccount bankAccount;

    @JsonIgnoreProperties(value = { "operation", "budgetItem" }, allowSetters = true)
    @OneToOne(mappedBy = "operation")
    @org.springframework.data.annotation.Transient
    private BudgetItemPeriod budgetItemPeriod;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Operation id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Operation label(String label) {
        this.setLabel(label);
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Operation date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Float getAmount() {
        return this.amount;
    }

    public Operation amount(Float amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getNote() {
        return this.note;
    }

    public Operation note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckNumber() {
        return this.checkNumber;
    }

    public Operation checkNumber(String checkNumber) {
        this.setCheckNumber(checkNumber);
        return this;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public Boolean getIsUpToDate() {
        return this.isUpToDate;
    }

    public Operation isUpToDate(Boolean isUpToDate) {
        this.setIsUpToDate(isUpToDate);
        return this;
    }

    public void setIsUpToDate(Boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    public Boolean getDeletingHardLock() {
        return this.deletingHardLock;
    }

    public Operation deletingHardLock(Boolean deletingHardLock) {
        this.setDeletingHardLock(deletingHardLock);
        return this;
    }

    public void setDeletingHardLock(Boolean deletingHardLock) {
        this.deletingHardLock = deletingHardLock;
    }

    public SubCategory getSubCategory() {
        return this.subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public Operation subCategory(SubCategory subCategory) {
        this.setSubCategory(subCategory);
        return this;
    }

    public ApplicationUser getAccount() {
        return this.account;
    }

    public void setAccount(ApplicationUser applicationUser) {
        this.account = applicationUser;
    }

    public Operation account(ApplicationUser applicationUser) {
        this.setAccount(applicationUser);
        return this;
    }

    public BankAccount getBankAccount() {
        return this.bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public Operation bankAccount(BankAccount bankAccount) {
        this.setBankAccount(bankAccount);
        return this;
    }

    public BudgetItemPeriod getBudgetItemPeriod() {
        return this.budgetItemPeriod;
    }

    public void setBudgetItemPeriod(BudgetItemPeriod budgetItemPeriod) {
        if (this.budgetItemPeriod != null) {
            this.budgetItemPeriod.setOperation(null);
        }
        if (budgetItemPeriod != null) {
            budgetItemPeriod.setOperation(this);
        }
        this.budgetItemPeriod = budgetItemPeriod;
    }

    public Operation budgetItemPeriod(BudgetItemPeriod budgetItemPeriod) {
        this.setBudgetItemPeriod(budgetItemPeriod);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Operation)) {
            return false;
        }
        return id != null && id.equals(((Operation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Operation{" +
            "id=" + getId() +
            ", label='" + getLabel() + "'" +
            ", date='" + getDate() + "'" +
            ", amount=" + getAmount() +
            ", note='" + getNote() + "'" +
            ", checkNumber='" + getCheckNumber() + "'" +
            ", isUpToDate='" + getIsUpToDate() + "'" +
            ", deletingHardLock='" + getDeletingHardLock() + "'" +
            "}";
    }
}
