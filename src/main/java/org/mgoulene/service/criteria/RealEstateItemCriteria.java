package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.RealEstateItem} entity. This class is used
 * in {@link org.mgoulene.web.rest.RealEstateItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /real-estate-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RealEstateItemCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private FloatFilter loanValue;

    private FloatFilter totalValue;

    private FloatFilter percentOwned;

    private LocalDateFilter itemDate;

    private LongFilter bankAccountId;

    private Boolean distinct;

    public RealEstateItemCriteria() {}

    public RealEstateItemCriteria(RealEstateItemCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.loanValue = other.loanValue == null ? null : other.loanValue.copy();
        this.totalValue = other.totalValue == null ? null : other.totalValue.copy();
        this.percentOwned = other.percentOwned == null ? null : other.percentOwned.copy();
        this.itemDate = other.itemDate == null ? null : other.itemDate.copy();
        this.bankAccountId = other.bankAccountId == null ? null : other.bankAccountId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RealEstateItemCriteria copy() {
        return new RealEstateItemCriteria(this);
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

    public FloatFilter getLoanValue() {
        return loanValue;
    }

    public FloatFilter loanValue() {
        if (loanValue == null) {
            loanValue = new FloatFilter();
        }
        return loanValue;
    }

    public void setLoanValue(FloatFilter loanValue) {
        this.loanValue = loanValue;
    }

    public FloatFilter getTotalValue() {
        return totalValue;
    }

    public FloatFilter totalValue() {
        if (totalValue == null) {
            totalValue = new FloatFilter();
        }
        return totalValue;
    }

    public void setTotalValue(FloatFilter totalValue) {
        this.totalValue = totalValue;
    }

    public FloatFilter getPercentOwned() {
        return percentOwned;
    }

    public FloatFilter percentOwned() {
        if (percentOwned == null) {
            percentOwned = new FloatFilter();
        }
        return percentOwned;
    }

    public void setPercentOwned(FloatFilter percentOwned) {
        this.percentOwned = percentOwned;
    }

    public LocalDateFilter getItemDate() {
        return itemDate;
    }

    public LocalDateFilter itemDate() {
        if (itemDate == null) {
            itemDate = new LocalDateFilter();
        }
        return itemDate;
    }

    public void setItemDate(LocalDateFilter itemDate) {
        this.itemDate = itemDate;
    }

    public LongFilter getBankAccountId() {
        return bankAccountId;
    }

    public LongFilter bankAccountId() {
        if (bankAccountId == null) {
            bankAccountId = new LongFilter();
        }
        return bankAccountId;
    }

    public void setBankAccountId(LongFilter bankAccountId) {
        this.bankAccountId = bankAccountId;
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
        final RealEstateItemCriteria that = (RealEstateItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(loanValue, that.loanValue) &&
            Objects.equals(totalValue, that.totalValue) &&
            Objects.equals(percentOwned, that.percentOwned) &&
            Objects.equals(itemDate, that.itemDate) &&
            Objects.equals(bankAccountId, that.bankAccountId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loanValue, totalValue, percentOwned, itemDate, bankAccountId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RealEstateItemCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (loanValue != null ? "loanValue=" + loanValue + ", " : "") +
            (totalValue != null ? "totalValue=" + totalValue + ", " : "") +
            (percentOwned != null ? "percentOwned=" + percentOwned + ", " : "") +
            (itemDate != null ? "itemDate=" + itemDate + ", " : "") +
            (bankAccountId != null ? "bankAccountId=" + bankAccountId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
