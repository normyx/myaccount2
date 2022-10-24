package org.mgoulene.mya.web.rest.vm;

import java.util.ArrayList;
import java.util.List;

public class MyaReportCategorySplit {

    private List<String> categoryNames;
    private List<Float> amounts;

    public MyaReportCategorySplit() {
        setAmounts(new ArrayList<Float>());
        setCategoryNames(new ArrayList<String>());
    }

    /**
     * @return the amounts
     */
    public List<Float> getAmounts() {
        return amounts;
    }

    /**
     * @param amounts the amounts to set
     */
    public void setAmounts(List<Float> amounts) {
        this.amounts = amounts;
    }

    public MyaReportCategorySplit addAmount(Float amount) {
        this.getAmounts().add(amount);
        return this;
    }

    /**
     * @return the categoryNames
     */
    public List<String> getCategoryNames() {
        return categoryNames;
    }

    /**
     * @param categoryNames the categoryNames to set
     */
    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public MyaReportCategorySplit addCategoryName(String categoryName) {
        this.getCategoryNames().add(categoryName);
        return this;
    }
}
