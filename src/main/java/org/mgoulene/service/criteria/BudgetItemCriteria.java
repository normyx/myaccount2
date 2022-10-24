package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.BudgetItem} entity. This class is used
 * in {@link org.mgoulene.web.rest.BudgetItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /budget-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BudgetItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private IntegerFilter order;

    private LongFilter budgetItemPeriodsId;

    private LongFilter categoryId;

    private LongFilter accountId;

    private Boolean distinct;

    public BudgetItemCriteria() {}

    public BudgetItemCriteria(BudgetItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.order = other.order == null ? null : other.order.copy();
        this.budgetItemPeriodsId = other.budgetItemPeriodsId == null ? null : other.budgetItemPeriodsId.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.accountId = other.accountId == null ? null : other.accountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BudgetItemCriteria copy() {
        return new BudgetItemCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public IntegerFilter getOrder() {
        return order;
    }

    public IntegerFilter order() {
        if (order == null) {
            order = new IntegerFilter();
        }
        return order;
    }

    public void setOrder(IntegerFilter order) {
        this.order = order;
    }

    public LongFilter getBudgetItemPeriodsId() {
        return budgetItemPeriodsId;
    }

    public LongFilter budgetItemPeriodsId() {
        if (budgetItemPeriodsId == null) {
            budgetItemPeriodsId = new LongFilter();
        }
        return budgetItemPeriodsId;
    }

    public void setBudgetItemPeriodsId(LongFilter budgetItemPeriodsId) {
        this.budgetItemPeriodsId = budgetItemPeriodsId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getAccountId() {
        return accountId;
    }

    public LongFilter accountId() {
        if (accountId == null) {
            accountId = new LongFilter();
        }
        return accountId;
    }

    public void setAccountId(LongFilter accountId) {
        this.accountId = accountId;
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
        final BudgetItemCriteria that = (BudgetItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(order, that.order) &&
            Objects.equals(budgetItemPeriodsId, that.budgetItemPeriodsId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, order, budgetItemPeriodsId, categoryId, accountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BudgetItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (order != null ? "order=" + order + ", " : "") +
            (budgetItemPeriodsId != null ? "budgetItemPeriodsId=" + budgetItemPeriodsId + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (accountId != null ? "accountId=" + accountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
