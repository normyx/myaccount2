package org.mgoulene.mya.service.dto;

public interface MyaDateDataPointData extends Cloneable {
    public void add(MyaDateDataPointData other);

    public void consolidate();

    public Object clone() throws CloneNotSupportedException;
}
