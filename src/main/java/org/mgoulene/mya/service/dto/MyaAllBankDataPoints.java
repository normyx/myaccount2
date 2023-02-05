package org.mgoulene.mya.service.dto;

import java.time.LocalDate;
import java.util.List;

public class MyaAllBankDataPoints {

    public List<LocalDate> getDates() {
        return dates;
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    private MyaDateDataSinglePoints realEstatePoints;
    private MyaDateDataSinglePoints savingsPoints;
    private MyaDateDataSinglePoints currentPoints;
    private MyaDateDataSinglePoints stockData;
    private List<LocalDate> dates;

    public MyaDateDataSinglePoints getRealEstatePoints() {
        return realEstatePoints;
    }

    public MyaAllBankDataPoints(
        MyaDateDataSinglePoints realEstatePoints,
        MyaDateDataSinglePoints savingsPoints,
        MyaDateDataSinglePoints currentPoints,
        MyaDateDataSinglePoints stockData
    ) {
        this.realEstatePoints = realEstatePoints;
        this.savingsPoints = savingsPoints;
        this.currentPoints = currentPoints;
        this.stockData = stockData;
        this.dates = realEstatePoints.getDates();
    }

    public void setRealEstatePoints(MyaDateDataSinglePoints realEstatePoints) {
        this.realEstatePoints = realEstatePoints;
    }

    public MyaDateDataSinglePoints getSavingsPoints() {
        return savingsPoints;
    }

    public void setSavingsPoints(MyaDateDataSinglePoints savingsPoints) {
        this.savingsPoints = savingsPoints;
    }

    public MyaDateDataSinglePoints getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(MyaDateDataSinglePoints currentPoints) {
        this.currentPoints = currentPoints;
    }

    public MyaDateDataSinglePoints getStockData() {
        return stockData;
    }

    public void setStockData(MyaDateDataSinglePoints stockData) {
        this.stockData = stockData;
    }
}
