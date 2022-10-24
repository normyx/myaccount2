package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.Operation} entity. This class is used
 * in {@link org.mgoulene.web.rest.OperationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /operations?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OperationCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter label;

    private LocalDateFilter date;

    private FloatFilter amount;

    private StringFilter note;

    private StringFilter checkNumber;

    private BooleanFilter isUpToDate;

    private BooleanFilter deletingHardLock;

    private LongFilter subCategoryId;

    private LongFilter accountId;

    private LongFilter bankAccountId;

    private LongFilter budgetItemPeriodId;

    private Boolean distinct;

    public OperationCriteria() {}

    public OperationCriteria(OperationCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.label = other.label == null ? null : other.label.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.amount = other.amount == null ? null : other.amount.copy();
        this.note = other.note == null ? null : other.note.copy();
        this.checkNumber = other.checkNumber == null ? null : other.checkNumber.copy();
        this.isUpToDate = other.isUpToDate == null ? null : other.isUpToDate.copy();
        this.deletingHardLock = other.deletingHardLock == null ? null : other.deletingHardLock.copy();
        this.subCategoryId = other.subCategoryId == null ? null : other.subCategoryId.copy();
        this.accountId = other.accountId == null ? null : other.accountId.copy();
        this.bankAccountId = other.bankAccountId == null ? null : other.bankAccountId.copy();
        this.budgetItemPeriodId = other.budgetItemPeriodId == null ? null : other.budgetItemPeriodId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public OperationCriteria copy() {
        return new OperationCriteria(this);
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

    public StringFilter getLabel() {
        return label;
    }

    public StringFilter label() {
        if (label == null) {
            label = new StringFilter();
        }
        return label;
    }

    public void setLabel(StringFilter label) {
        this.label = label;
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

    public StringFilter getNote() {
        return note;
    }

    public StringFilter note() {
        if (note == null) {
            note = new StringFilter();
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public StringFilter getCheckNumber() {
        return checkNumber;
    }

    public StringFilter checkNumber() {
        if (checkNumber == null) {
            checkNumber = new StringFilter();
        }
        return checkNumber;
    }

    public void setCheckNumber(StringFilter checkNumber) {
        this.checkNumber = checkNumber;
    }

    public BooleanFilter getIsUpToDate() {
        return isUpToDate;
    }

    public BooleanFilter isUpToDate() {
        if (isUpToDate == null) {
            isUpToDate = new BooleanFilter();
        }
        return isUpToDate;
    }

    public void setIsUpToDate(BooleanFilter isUpToDate) {
        this.isUpToDate = isUpToDate;
    }

    public BooleanFilter getDeletingHardLock() {
        return deletingHardLock;
    }

    public BooleanFilter deletingHardLock() {
        if (deletingHardLock == null) {
            deletingHardLock = new BooleanFilter();
        }
        return deletingHardLock;
    }

    public void setDeletingHardLock(BooleanFilter deletingHardLock) {
        this.deletingHardLock = deletingHardLock;
    }

    public LongFilter getSubCategoryId() {
        return subCategoryId;
    }

    public LongFilter subCategoryId() {
        if (subCategoryId == null) {
            subCategoryId = new LongFilter();
        }
        return subCategoryId;
    }

    public void setSubCategoryId(LongFilter subCategoryId) {
        this.subCategoryId = subCategoryId;
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

    public LongFilter getBudgetItemPeriodId() {
        return budgetItemPeriodId;
    }

    public LongFilter budgetItemPeriodId() {
        if (budgetItemPeriodId == null) {
            budgetItemPeriodId = new LongFilter();
        }
        return budgetItemPeriodId;
    }

    public void setBudgetItemPeriodId(LongFilter budgetItemPeriodId) {
        this.budgetItemPeriodId = budgetItemPeriodId;
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
        final OperationCriteria that = (OperationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(label, that.label) &&
            Objects.equals(date, that.date) &&
            Objects.equals(amount, that.amount) &&
            Objects.equals(note, that.note) &&
            Objects.equals(checkNumber, that.checkNumber) &&
            Objects.equals(isUpToDate, that.isUpToDate) &&
            Objects.equals(deletingHardLock, that.deletingHardLock) &&
            Objects.equals(subCategoryId, that.subCategoryId) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(bankAccountId, that.bankAccountId) &&
            Objects.equals(budgetItemPeriodId, that.budgetItemPeriodId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            label,
            date,
            amount,
            note,
            checkNumber,
            isUpToDate,
            deletingHardLock,
            subCategoryId,
            accountId,
            bankAccountId,
            budgetItemPeriodId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OperationCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (label != null ? "label=" + label + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (amount != null ? "amount=" + amount + ", " : "") +
            (note != null ? "note=" + note + ", " : "") +
            (checkNumber != null ? "checkNumber=" + checkNumber + ", " : "") +
            (isUpToDate != null ? "isUpToDate=" + isUpToDate + ", " : "") +
            (deletingHardLock != null ? "deletingHardLock=" + deletingHardLock + ", " : "") +
            (subCategoryId != null ? "subCategoryId=" + subCategoryId + ", " : "") +
            (accountId != null ? "accountId=" + accountId + ", " : "") +
            (bankAccountId != null ? "bankAccountId=" + bankAccountId + ", " : "") +
            (budgetItemPeriodId != null ? "budgetItemPeriodId=" + budgetItemPeriodId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
