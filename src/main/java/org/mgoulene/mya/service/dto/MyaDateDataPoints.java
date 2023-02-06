package org.mgoulene.mya.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyaDateDataPoints<V extends MyaDateDataPointsProperties, D extends MyaDateDataPointData> {

    private final Logger log = LoggerFactory.getLogger(MyaDateDataPoints.class);
    private V properties;
    private List<MyaDateDataPoint<D>> points;

    public MyaDateDataPoints(V properties) {
        this.properties = properties;
        points = new ArrayList<>();
    }

    public void addDatePoint(LocalDate date) {
        points.add(new MyaDateDataPoint<D>(date));
    }

    public void addPoint(MyaDateDataPoint<D> p) {
        points.add(p);
    }

    public V getProperties() {
        return properties;
    }

    public List<MyaDateDataPoint<D>> getPoints() {
        return points;
    }

    @JsonIgnore
    public List<LocalDate> getDates() {
        ArrayList<LocalDate> dates = new ArrayList<>();
        for (MyaDateDataPoint<D> p : this.getPoints()) {
            dates.add(p.getDate());
        }
        return dates;
    }

    public MyaDateDataPoint<D> getDateDataPointCloseToDate(MyaDateDataPoint<D> other) {
        if (points.size() != 0) {
            if (points.get(0).isAfter(other)) {
                return null;
            }
            for (int i = 0; i < points.size(); i++) {
                MyaDateDataPoint<D> p = points.get(i);
                if (other.isSameDate(p)) {
                    return p;
                } else {
                    if (
                        other.isAfter(p) &&
                        (
                            (
                                i != points.size() - 1 &&
                                (other.isBefore(points.get(i + 1)) || other.isSameDate(points.get(i + 1))) ||
                                i == points.size() - 1
                            )
                        )
                    ) {
                        return p;
                    }
                }
            }
        }

        return null;
    }

    public List<LocalDate> getOrderedMergedDates(MyaDateDataPoints<V, D> otherPoints) {
        HashMap<LocalDate, Object> dateKeys = new HashMap<>();
        for (MyaDateDataPoint<D> p : otherPoints.getPoints()) {
            dateKeys.put(p.getDate(), null);
        }
        for (MyaDateDataPoint<D> p : this.getPoints()) {
            dateKeys.put(p.getDate(), null);
        }
        ArrayList<LocalDate> dates = new ArrayList<>(dateKeys.keySet());
        Collections.sort(dates, new MyaLocalDateComparator());
        return dates;
    }

    public void normalize(List<LocalDate> dates) {
        MyaDateDataPoints<V, D> newPoints = new MyaDateDataPoints<V, D>(getProperties());
        int index = 0;
        MyaDateDataPoint<D> current = null;
        for (LocalDate date : dates) {
            if (index == 0 && getPoints().get(0).isAfter(date)) {
                current = new MyaDateDataPoint<>(date);
                index++;
            } else if (index < getPoints().size() && (getPoints().get(index).isSameDate(date) || getPoints().get(index).isBefore(date))) {
                current = getPoints().get(index);
                if (index != getPoints().size() - 1) index++;
            }
            MyaDateDataPoint<D> newPoint = null;
            try {
                newPoint = new MyaDateDataPoint<>(date, current.getData() != null ? (D) current.getData().clone() : null);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            newPoints.addPoint(newPoint);
        }

        this.points = newPoints.getPoints();
    }

    public void merge(MyaDateDataPoints<V, D> otherPoints) {
        List<LocalDate> dates = getOrderedMergedDates(otherPoints);
        this.normalize(dates);
        otherPoints.normalize(dates);
        for (int i = 0; i < otherPoints.getPoints().size(); i++) {
            this.points.get(i).addData(otherPoints.getPoints().get(i).getData());
        }
    }

    @Override
    public String toString() {
        return "MyaDateDataPoints [properties=" + properties + ", points=" + points + "]";
    }
}
