package org.mgoulene.mya.domain;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

public class MyaReportMonthlyData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Long accountId;

    private LocalDate month;

    private Long categoryId;

    private String categoryName;

    private Float budgetAmount;

    private Float amount;

    private Float amountAvg3;

    private Float amountAvg12;

    public MyaReportMonthlyData(Object[] initData) {
        convert(initData);
    }

    private void convert(Object[] initData) {
        this.id = (String) initData[0];
        this.accountId = initData[1] != null ? ((BigInteger) initData[1]).longValue() : null;
        this.month = initData[2] != null ? ((Date) initData[2]).toLocalDate() : null;
        this.categoryId = initData[3] != null ? ((Number) initData[3]).longValue() : null;
        this.categoryName = initData[4] != null ? (String) initData[4] : null;
        this.budgetAmount = initData[5] != null ? ((Double) initData[5]).floatValue() : null;
        this.amount = initData[6] != null ? ((Double) initData[6]).floatValue() : null;
        this.amountAvg3 = initData[7] != null ? ((Double) initData[7]).floatValue() : null;
        this.amountAvg12 = initData[8] != null ? ((Double) initData[8]).floatValue() : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Float getAmountAvg3() {
        return amountAvg3;
    }

    public void setAmountAvg3(Float amountAvg3) {
        this.amountAvg3 = amountAvg3;
    }

    public Float getAmountAvg12() {
        return amountAvg12;
    }

    public void setAmountAvg12(Float amountAvg12) {
        this.amountAvg12 = amountAvg12;
    }

    public Float getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(Float budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MyaReportMonthlyData amr = (MyaReportMonthlyData) o;
        if (amr.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), amr.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return (
            "ReportMonthlyData{" +
            "id=" +
            getId() +
            ", month='" +
            getMonth() +
            "'" +
            ", amount=" +
            getAmount() +
            ", amountAvg3=" +
            getAmountAvg3() +
            ", amountAvg12=" +
            getAmountAvg12() +
            ", budgetAmount=" +
            getBudgetAmount() +
            "}"
        );
    }
}
