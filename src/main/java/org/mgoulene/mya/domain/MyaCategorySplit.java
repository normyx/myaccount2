package org.mgoulene.mya.domain;

import java.io.Serializable;

public class MyaCategorySplit implements Serializable {

    private static final long serialVersionUID = 1L;

    public MyaCategorySplit() {}

    private String categoryName;

    private Float amount;

    /**
     * @return the amount
     */
    public Float getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public MyaCategorySplit amount(Object amount) {
        setAmount(amount != null ? ((Double) amount).floatValue() : null);
        return this;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName the categoryName to set
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public MyaCategorySplit categoryName(Object categoryName) {
        setCategoryName(categoryName != null ? (String) categoryName : null);
        return this;
    }
}
