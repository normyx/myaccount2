package org.mgoulene.mya.domain;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

public class MyaReportAmountsByDates implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDate date;
    private Float amount;
    private Float predictiveAmount;

    public MyaReportAmountsByDates() {}

    /**
     * @return the predictiveAmount
     */
    public Float getPredictiveAmount() {
        return predictiveAmount;
    }

    /**
     * @param predictiveAmount the predictiveAmount to set
     */
    public void setPredictiveAmount(Float predictiveAmount) {
        this.predictiveAmount = predictiveAmount;
    }

    public MyaReportAmountsByDates predictiveAmount(Object predictiveAmount) {
        setPredictiveAmount(predictiveAmount != null ? ((Number) predictiveAmount).floatValue() : null);
        return this;
    }

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

    public MyaReportAmountsByDates amount(Object amount) {
        setAmount(amount != null ? ((Number) amount).floatValue() : null);
        return this;
    }

    /**
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public MyaReportAmountsByDates date(Object date) {
        setDate(date != null ? ((Date) date).toLocalDate() : null);
        return this;
    }
}
