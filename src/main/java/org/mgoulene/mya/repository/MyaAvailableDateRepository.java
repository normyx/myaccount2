package org.mgoulene.mya.repository;

import java.time.LocalDate;
import java.util.List;

public interface MyaAvailableDateRepository {
    public List<LocalDate> findAllMonthFrom(LocalDate fromMonth);
}
