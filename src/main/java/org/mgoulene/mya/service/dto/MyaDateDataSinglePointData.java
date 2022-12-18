package org.mgoulene.mya.service.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataSinglePointData implements MyaDateDataPointData {

    private final Logger log = LoggerFactory.getLogger(MyaDateDataSinglePointData.class);

    private float value = 0;

    public MyaDateDataSinglePointData(float value) {
        this.value = value;
    }

    @Override
    public void add(MyaDateDataPointData other) {
        if (other != null) {
            MyaDateDataSinglePointData myaDateDataSinglePointData = (MyaDateDataSinglePointData) other;

            this.value += myaDateDataSinglePointData.getValue();
            this.consolidate();
        }
    }

    @Override
    public void consolidate() {}

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
