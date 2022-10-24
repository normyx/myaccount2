package org.mgoulene.mya.web.rest.vm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyaAccountMonthReportData {

    private Long accountId;
    private Long categoryId;
    private String categoryName;
    private List<LocalDate> months;
    private List<Float> amounts;
    private List<Float> amountsAvg3;
    private List<Float> amountsAvg12;
    private List<Float> budgetAmounts;

    public MyaAccountMonthReportData(Long accountId, Long categoryId, String categoryName) {
        this.setAccountId(accountId);
        this.setCategoryId(categoryId);
        this.setCategoryName(categoryName);
        setMonths(new ArrayList<LocalDate>());
        setAmounts(new ArrayList<Float>());
        setAmountsAvg3(new ArrayList<Float>());
        setAmountsAvg12(new ArrayList<Float>());
        setBudgetAmounts(new ArrayList<Float>());
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

    public List<LocalDate> getMonths() {
        return months;
    }

    public void setMonths(List<LocalDate> months) {
        this.months = months;
    }

    public MyaAccountMonthReportData addMonth(LocalDate month) {
        this.getMonths().add(month);
        return this;
    }

    public List<Float> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Float> amounts) {
        this.amounts = amounts;
    }

    public MyaAccountMonthReportData addAmount(Float amount) {
        this.getAmounts().add(amount);
        return this;
    }

    public List<Float> getAmountsAvg3() {
        return amountsAvg3;
    }

    public void setAmountsAvg3(List<Float> amountsAvg3) {
        this.amountsAvg3 = amountsAvg3;
    }

    public MyaAccountMonthReportData addAmountAvg3(Float amount) {
        this.getAmountsAvg3().add(amount);
        return this;
    }

    public List<Float> getAmountsAvg12() {
        return amountsAvg12;
    }

    public void setAmountsAvg12(List<Float> amountsAvg12) {
        this.amountsAvg12 = amountsAvg12;
    }

    public MyaAccountMonthReportData addAmountAvg12(Float amount) {
        this.getAmountsAvg12().add(amount);
        return this;
    }

    public List<Float> getBudgetAmounts() {
        return budgetAmounts;
    }

    public void setBudgetAmounts(List<Float> budgetAmounts) {
        this.budgetAmounts = budgetAmounts;
    }

    public MyaAccountMonthReportData addBudgetAmount(Float amount) {
        this.getBudgetAmounts().add(amount);
        return this;
    }
}
