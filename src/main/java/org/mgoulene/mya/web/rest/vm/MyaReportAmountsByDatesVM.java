package org.mgoulene.mya.web.rest.vm;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MyaReportAmountsByDatesVM {

    private List<LocalDate> dates;
    private List<Float> amounts;
    private List<Float> predictiveAmounts;

    public MyaReportAmountsByDatesVM() {
        setDates(new ArrayList<LocalDate>());
        setAmounts(new ArrayList<Float>());
        setPredictiveAmounts(new ArrayList<Float>());
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    public MyaReportAmountsByDatesVM addDate(LocalDate date) {
        this.getDates().add(date);
        return this;
    }

    public List<Float> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Float> amounts) {
        this.amounts = amounts;
    }

    public MyaReportAmountsByDatesVM addAmount(Float amount) {
        this.getAmounts().add(amount);
        return this;
    }

    public List<Float> getPredictiveAmounts() {
        return predictiveAmounts;
    }

    public void setPredictiveAmounts(List<Float> predictiveAmounts) {
        this.predictiveAmounts = predictiveAmounts;
    }

    public MyaReportAmountsByDatesVM addPredictiveAmounts(Float predictiveAmounts) {
        this.getPredictiveAmounts().add(predictiveAmounts);
        return this;
    }
}
