package org.mgoulene.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.mgoulene.domain.enumeration.BankAccountType;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link org.mgoulene.domain.BankAccount} entity. This class is used
 * in {@link org.mgoulene.web.rest.BankAccountResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bank-accounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BankAccountCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BankAccountType
     */
    public static class BankAccountTypeFilter extends Filter<BankAccountType> {

        public BankAccountTypeFilter() {}

        public BankAccountTypeFilter(BankAccountTypeFilter filter) {
            super(filter);
        }

        @Override
        public BankAccountTypeFilter copy() {
            return new BankAccountTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter accountName;

    private StringFilter accountBank;

    private FloatFilter initialAmount;

    private BooleanFilter archived;

    private StringFilter shortName;

    private BankAccountTypeFilter accountType;

    private LongFilter accountId;

    private LongFilter stockPortfolioItemId;

    private LongFilter realEstateItemId;

    private Boolean distinct;

    public BankAccountCriteria() {}

    public BankAccountCriteria(BankAccountCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.accountName = other.accountName == null ? null : other.accountName.copy();
        this.accountBank = other.accountBank == null ? null : other.accountBank.copy();
        this.initialAmount = other.initialAmount == null ? null : other.initialAmount.copy();
        this.archived = other.archived == null ? null : other.archived.copy();
        this.shortName = other.shortName == null ? null : other.shortName.copy();
        this.accountType = other.accountType == null ? null : other.accountType.copy();
        this.accountId = other.accountId == null ? null : other.accountId.copy();
        this.stockPortfolioItemId = other.stockPortfolioItemId == null ? null : other.stockPortfolioItemId.copy();
        this.realEstateItemId = other.realEstateItemId == null ? null : other.realEstateItemId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public BankAccountCriteria copy() {
        return new BankAccountCriteria(this);
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

    public StringFilter getAccountName() {
        return accountName;
    }

    public StringFilter accountName() {
        if (accountName == null) {
            accountName = new StringFilter();
        }
        return accountName;
    }

    public void setAccountName(StringFilter accountName) {
        this.accountName = accountName;
    }

    public StringFilter getAccountBank() {
        return accountBank;
    }

    public StringFilter accountBank() {
        if (accountBank == null) {
            accountBank = new StringFilter();
        }
        return accountBank;
    }

    public void setAccountBank(StringFilter accountBank) {
        this.accountBank = accountBank;
    }

    public FloatFilter getInitialAmount() {
        return initialAmount;
    }

    public FloatFilter initialAmount() {
        if (initialAmount == null) {
            initialAmount = new FloatFilter();
        }
        return initialAmount;
    }

    public void setInitialAmount(FloatFilter initialAmount) {
        this.initialAmount = initialAmount;
    }

    public BooleanFilter getArchived() {
        return archived;
    }

    public BooleanFilter archived() {
        if (archived == null) {
            archived = new BooleanFilter();
        }
        return archived;
    }

    public void setArchived(BooleanFilter archived) {
        this.archived = archived;
    }

    public StringFilter getShortName() {
        return shortName;
    }

    public StringFilter shortName() {
        if (shortName == null) {
            shortName = new StringFilter();
        }
        return shortName;
    }

    public void setShortName(StringFilter shortName) {
        this.shortName = shortName;
    }

    public BankAccountTypeFilter getAccountType() {
        return accountType;
    }

    public BankAccountTypeFilter accountType() {
        if (accountType == null) {
            accountType = new BankAccountTypeFilter();
        }
        return accountType;
    }

    public void setAccountType(BankAccountTypeFilter accountType) {
        this.accountType = accountType;
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

    public LongFilter getStockPortfolioItemId() {
        return stockPortfolioItemId;
    }

    public LongFilter stockPortfolioItemId() {
        if (stockPortfolioItemId == null) {
            stockPortfolioItemId = new LongFilter();
        }
        return stockPortfolioItemId;
    }

    public void setStockPortfolioItemId(LongFilter stockPortfolioItemId) {
        this.stockPortfolioItemId = stockPortfolioItemId;
    }

    public LongFilter getRealEstateItemId() {
        return realEstateItemId;
    }

    public LongFilter realEstateItemId() {
        if (realEstateItemId == null) {
            realEstateItemId = new LongFilter();
        }
        return realEstateItemId;
    }

    public void setRealEstateItemId(LongFilter realEstateItemId) {
        this.realEstateItemId = realEstateItemId;
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
        final BankAccountCriteria that = (BankAccountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(accountName, that.accountName) &&
            Objects.equals(accountBank, that.accountBank) &&
            Objects.equals(initialAmount, that.initialAmount) &&
            Objects.equals(archived, that.archived) &&
            Objects.equals(shortName, that.shortName) &&
            Objects.equals(accountType, that.accountType) &&
            Objects.equals(accountId, that.accountId) &&
            Objects.equals(stockPortfolioItemId, that.stockPortfolioItemId) &&
            Objects.equals(realEstateItemId, that.realEstateItemId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            accountName,
            accountBank,
            initialAmount,
            archived,
            shortName,
            accountType,
            accountId,
            stockPortfolioItemId,
            realEstateItemId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BankAccountCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (accountName != null ? "accountName=" + accountName + ", " : "") +
            (accountBank != null ? "accountBank=" + accountBank + ", " : "") +
            (initialAmount != null ? "initialAmount=" + initialAmount + ", " : "") +
            (archived != null ? "archived=" + archived + ", " : "") +
            (shortName != null ? "shortName=" + shortName + ", " : "") +
            (accountType != null ? "accountType=" + accountType + ", " : "") +
            (accountId != null ? "accountId=" + accountId + ", " : "") +
            (stockPortfolioItemId != null ? "stockPortfolioItemId=" + stockPortfolioItemId + ", " : "") +
            (realEstateItemId != null ? "realEstateItemId=" + realEstateItemId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
