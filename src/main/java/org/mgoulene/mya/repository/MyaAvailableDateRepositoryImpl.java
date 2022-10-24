package org.mgoulene.mya.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unused")
public class MyaAvailableDateRepositoryImpl implements MyaAvailableDateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<LocalDate> findAllMonthFrom(LocalDate fromMonth) {
        Query querySelect = entityManager.createNativeQuery(
            "SELECT DISTINCT month FROM param_days WHERE month >= :fromMonth ORDER BY month ASC"
        );
        querySelect.setParameter("fromMonth", fromMonth);
        List<Date> months = querySelect.getResultList();
        List<LocalDate> returns = new ArrayList<>();
        for (Date month : months) {
            returns.add(month.toLocalDate());
        }
        return returns;
    }
}
