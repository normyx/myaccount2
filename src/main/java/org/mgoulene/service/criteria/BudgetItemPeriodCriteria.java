package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.BudgetItemPeriod} entity. This class is used
 * in {@link org.mgoulene.web.rest.BudgetItemPeriodResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /budget-item-periods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItemPeriodCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter date;

    private LocalDateFilter month;

    private FloatFilter amount;

    private BooleanFilter isSmoothed;

    private BooleanFilter isRecurrent;

    private LongFilter operationId;

    private LongFilter budgetItemId;

    private Boolean distinct;

    public BudgetItemPeriodCriteria() {}

    public BudgetItemPeriodCriteria(BudgetItemPeriodCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.month = other.month == null ? null : other.month.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.isSmoothed = other.isSmoothed == null ? null : other.isSmoothed.copy();
        this.isRecurrent = other.isRecurrent == null ? null : other.isRecurrent.copy();
        this.operationId = other.operationId == null ? null : other.operationId.copy();
        this.budgetItemId = other.budgetItemId == null ? null : other.budgetItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BudgetItemPeriodCriteria copy() {
        return new BudgetItemPeriodCriteria(this);
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

    public LocalDateFilter getDate() {
        return date;
    }

    public LocalDateFilter date() {
        if (date == null) {
            date = new LocalDateFilter();
        }
        return date;
    }

    public void setDate(LocalDateFilter date) {
        this.date = date;
    }

    public LocalDateFilter getMonth() {
        return month;
    }

    public LocalDateFilter month() {
        if (month == null) {
            month = new LocalDateFilter();
        }
        return month;
    }

    public void setMonth(LocalDateFilter month) {
        this.month = month;
    }

    public FloatFilter getAmount() {
        return amount;
    }

    public FloatFilter amount() {
        if (amount == null) {
            amount = new FloatFilter();
        }
        return amount;
    }

    public void setAmount(FloatFilter amount) {
        this.amount = amount;
    }

    public BooleanFilter getIsSmoothed() {
        return isSmoothed;
    }

    public BooleanFilter isSmoothed() {
        if (isSmoothed == null) {
            isSmoothed = new BooleanFilter();
        }
        return isSmoothed;
    }

    public void setIsSmoothed(BooleanFilter isSmoothed) {
        this.isSmoothed = isSmoothed;
    }

    public BooleanFilter getIsRecurrent() {
        return isRecurrent;
    }

    public BooleanFilter isRecurrent() {
        if (isRecurrent == null) {
            isRecurrent = new BooleanFilter();
        }
        return isRecurrent;
    }

    public void setIsRecurrent(BooleanFilter isRecurrent) {
        this.isRecurrent = isRecurrent;
    }

    public LongFilter getOperationId() {
        return operationId;
    }

    public LongFilter operationId() {
        if (operationId == null) {
            operationId = new LongFilter();
        }
        return operationId;
    }

    public void setOperationId(LongFilter operationId) {
        this.operationId = operationId;
    }

    public LongFilter getBudgetItemId() {
        return budgetItemId;
    }

    public LongFilter budgetItemId() {
        if (budgetItemId == null) {
            budgetItemId = new LongFilter();
        }
        return budgetItemId;
    }

    public void setBudgetItemId(LongFilter budgetItemId) {
        this.budgetItemId = budgetItemId;
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
        final BudgetItemPeriodCriteria that = (BudgetItemPeriodCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(date, that.date) &&
            Objects.equals(month, that.month) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(isSmoothed, that.isSmoothed) &&
            Objects.equals(isRecurrent, that.isRecurrent) &&
            Objects.equals(operationId, that.operationId) &&
            Objects.equals(budgetItemId, that.budgetItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, month, amount, isSmoothed, isRecurrent, operationId, budgetItemId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItemPeriodCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (month != null ? "month=" + month + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (isSmoothed != null ? "isSmoothed=" + isSmoothed + ", " : "") +
            (isRecurrent != null ? "isRecurrent=" + isRecurrent + ", " : "") +
            (operationId != null ? "operationId=" + operationId + ", " : "") +
            (budgetItemId != null ? "budgetItemId=" + budgetItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
