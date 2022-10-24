package org.mgoulene.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BudgetItemPeriod.
 */
@Entity
@Table(name = "budget_item_period")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "budgetitemperiod")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItemPeriod implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "jhi_date")
    private LocalDate date;

    @NotNull
    @Column(name = "month", nullable = false)
    private LocalDate month;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "is_smoothed")
    private Boolean isSmoothed;

    @Column(name = "is_recurrent")
    private Boolean isRecurrent;

    @JsonIgnoreProperties(value = { "subCategory", "account", "bankAccount", "budgetItemPeriod" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Operation operation;

    @ManyToOne
    @JsonIgnoreProperties(value = { "budgetItemPeriods", "category", "account" }, allowSetters = true)
    private BudgetItem budgetItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BudgetItemPeriod id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public BudgetItemPeriod date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getMonth() {
        return this.month;
    }

    public BudgetItemPeriod month(LocalDate month) {
        this.setMonth(month);
        return this;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public Float getAmount() {
        return this.amount;
    }

    public BudgetItemPeriod amount(Float amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Boolean getIsSmoothed() {
        return this.isSmoothed;
    }

    public BudgetItemPeriod isSmoothed(Boolean isSmoothed) {
        this.setIsSmoothed(isSmoothed);
        return this;
    }

    public void setIsSmoothed(Boolean isSmoothed) {
        this.isSmoothed = isSmoothed;
    }

    public Boolean getIsRecurrent() {
        return this.isRecurrent;
    }

    public BudgetItemPeriod isRecurrent(Boolean isRecurrent) {
        this.setIsRecurrent(isRecurrent);
        return this;
    }

    public void setIsRecurrent(Boolean isRecurrent) {
        this.isRecurrent = isRecurrent;
    }

    public Operation getOperation() {
        return this.operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public BudgetItemPeriod operation(Operation operation) {
        this.setOperation(operation);
        return this;
    }

    public BudgetItem getBudgetItem() {
        return this.budgetItem;
    }

    public void setBudgetItem(BudgetItem budgetItem) {
        this.budgetItem = budgetItem;
    }

    public BudgetItemPeriod budgetItem(BudgetItem budgetItem) {
        this.setBudgetItem(budgetItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetItemPeriod)) {
            return false;
        }
        return id != null && id.equals(((BudgetItemPeriod) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItemPeriod{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", month='" + getMonth() + "'" +
            ", amount=" + getAmount() +
            ", isSmoothed='" + getIsSmoothed() + "'" +
            ", isRecurrent='" + getIsRecurrent() + "'" +
            "}";
    }
}
