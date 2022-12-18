package org.mgoulene.mya.service.dto;

import java.time.LocalDate;

public class MyaDateDataPoint<D extends MyaDateDataPointData> {

    private final LocalDate date;
    private D data;

    public MyaDateDataPoint(LocalDate date, D data) {
        this.date = date;
        this.data = data;
    }

    public MyaDateDataPoint(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public boolean isSameDate(MyaDateDataPoint<D> other) {
        if (other != null) {
            return date.isEqual(other.getDate());
        }
        return false;
    }

    public boolean isSameDate(LocalDate d) {
        if (d != null) {
            return date.isEqual(d);
        }
        return false;
    }

    public boolean isSameDateOrAfter(LocalDate d) {
        if (d != null) {
            return date.isEqual(d) || date.isAfter(d);
        }
        System.out.println("Date null");
        return false;
    }

    public boolean isAfter(LocalDate d) {
        if (d != null) {
            return date.isAfter(d);
        }
        System.out.println("Date null");
        return false;
    }

    public boolean isAfter(MyaDateDataPoint<D> other) {
        if (other != null) {
            return date.isAfter(other.getDate());
        }
        return false;
    }

    public boolean isBefore(MyaDateDataPoint<D> other) {
        if (other != null) {
            return date.isBefore(other.getDate());
        }
        return false;
    }

    public boolean isBefore(LocalDate d) {
        if (d != null) {
            return date.isBefore(d);
        }
        System.out.println("Date null");
        return false;
    }

    public void addData(D other) {
        if (this.data != null) {
            this.data.add(other);
        } else {
            this.data = other;
        }
    }

    @Override
    public String toString() {
        return "MyaDateDataPoint [date=" + date + ", data=" + data + "]";
    }
}
