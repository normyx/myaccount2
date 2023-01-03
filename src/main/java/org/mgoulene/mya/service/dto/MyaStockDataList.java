package org.mgoulene.mya.service.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.StockMarketData;

public class MyaStockDataList extends ArrayList<StockMarketData> {

    public MyaStockDataList(List<StockMarketData> c) {
        super(c);
    }

    public Optional<StockMarketData> getFromDate(LocalDate date) {
        if (size() > 0) {
            if (get(0).getDataDate().isAfter(date)) {
                return Optional.empty();
            }
            StockMarketData previous = null;
            for (StockMarketData element : this) {
                if (element.getDataDate().isEqual(date)) {
                    return Optional.of(element);
                }
                if (element.getDataDate().isAfter(date)) {
                    if (previous != null) {
                        return Optional.of(previous);
                    } else {
                        throw new Error("StockMarketData : Option Impossible");
                    }
                }
                previous = element;
            }
        }
        return Optional.empty();
        // throw new Error("StockMarketData not found");
    }
}
