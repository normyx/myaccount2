package org.mgoulene.mya.service.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataSinglePoints extends MyaDateDataPoints<MyaDateDataPointsSingleProperties, MyaDateDataSinglePointData> {

    public MyaDateDataSinglePoints(MyaDateDataPointsSingleProperties properties) {
        super(properties);
    }

    private final Logger log = LoggerFactory.getLogger(MyaDateDataStockPoints.class);
}
