package org.mgoulene.mya.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.io.IOUtils;
import org.mgoulene.mya.domain.MyaCategorySplit;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.domain.MyaReportDateEvolutionData;
import org.mgoulene.mya.domain.MyaReportMonthlyData;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class MyaReportDataRepositoryImpl implements MyaReportDataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // @Value("${spring.jpa.database}")
    private String databaseDialect = "MYSQL";

    private String selectDailyReportDataWhereMonthQuery;

    private String selectDailyReportDataWhereMonthAndCategoryQuery;

    private String selectMonthlyReportDataWhereCategoryBetweenMonthQuery;

    private String selectMonthlyReportDataBetweenMonthQuery;

    private String selectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarkedQuery;

    private String selectSubCategorySplitQuery;

    private String selectAmountsBetweenDatesQuery;

    private String selectAmountsBetweenDatesForBankAccountQuery;

    private String loadQuery(String queryName) {
        try {
            InputStream is;
            is = new ClassPathResource(getQueryFileName(queryName)).getInputStream();
            return IOUtils.toString(is, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getQueryFileName(String queryName) {
        return "./sql/" + queryName + "-" + databaseDialect + ".sql";
    }

    private synchronized String getSelectDailyReportDataWhereMonthQuery() {
        if (selectDailyReportDataWhereMonthQuery == null) {
            selectDailyReportDataWhereMonthQuery = loadQuery("select_daily_data_where_month");
        }
        return selectDailyReportDataWhereMonthQuery;
    }

    private synchronized String getSelectDailyReportDataWhereMonthAndCategoryQuery() {
        if (selectDailyReportDataWhereMonthAndCategoryQuery == null) {
            selectDailyReportDataWhereMonthAndCategoryQuery = loadQuery("select_daily_data_where_month_category");
        }
        return selectDailyReportDataWhereMonthAndCategoryQuery;
    }

    private synchronized String getSelectMonthlyReportDataWhereCategoryBetweenMonth() {
        if (selectMonthlyReportDataWhereCategoryBetweenMonthQuery == null) {
            selectMonthlyReportDataWhereCategoryBetweenMonthQuery = loadQuery("select_monthly_report_data_where_category_between_month");
        }
        return selectMonthlyReportDataWhereCategoryBetweenMonthQuery;
    }

    private synchronized String getSelectMonthlyReportDataBetweenMonth() {
        if (selectMonthlyReportDataBetweenMonthQuery == null) {
            selectMonthlyReportDataBetweenMonthQuery = loadQuery("select_monthly_report_data_between_month");
        }
        return selectMonthlyReportDataBetweenMonthQuery;
    }

    private synchronized String getSelectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked() {
        if (selectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarkedQuery == null) {
            selectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarkedQuery = loadQuery("select_monthly_report_data_with_unmarked");
        }
        return selectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarkedQuery;
    }

    private synchronized String getSelectSubCategorySplitQuery() {
        if (selectSubCategorySplitQuery == null) {
            selectSubCategorySplitQuery = loadQuery("select_subcat_split");
        }
        return selectSubCategorySplitQuery;
    }

    private synchronized String getSelectAmountsBetweenDatesQuery() {
        if (selectAmountsBetweenDatesQuery == null) {
            selectAmountsBetweenDatesQuery = loadQuery("select_amounts_between_dates");
        }
        return selectAmountsBetweenDatesQuery;
    }

    private synchronized String getSelectAmountsBetweenDatesForBankAccountQuery() {
        if (selectAmountsBetweenDatesForBankAccountQuery == null) {
            selectAmountsBetweenDatesForBankAccountQuery = loadQuery("select_amounts_between_dates_for_bank_account");
        }
        return selectAmountsBetweenDatesForBankAccountQuery;
    }

    private MyaReportDateEvolutionData convertDailyResultsToReportDateEvolutionData(Object[] res) {
        return new MyaReportDateEvolutionData()
            .id(res[0])
            .date(res[1])
            .month(res[2])
            .categoryId(res[4])
            .categoryName(res[5])
            .hasOperation(res[6])
            .operationAmount(res[7])
            .budgetSmoothedAmount(res[8])
            .budgetUnSmoothedUnMarkedAmount(res[9])
            .budgetUnSmoothedMarkedAmount(res[10]);
    }

    public List<MyaReportDateEvolutionData> findReportDataWhereMonth(Long accountId, LocalDate month) {
        Query querySelect = entityManager.createNativeQuery(getSelectDailyReportDataWhereMonthQuery());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("month", month);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportDateEvolutionData> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(convertDailyResultsToReportDateEvolutionData(res));
        }
        return rdedResults;
    }

    public List<MyaReportDateEvolutionData> findReportDataWhereMonthAndCategory(Long accountId, LocalDate month, Long categoryId) {
        Query querySelect = entityManager.createNativeQuery(getSelectDailyReportDataWhereMonthAndCategoryQuery());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("month", month);
        querySelect.setParameter("categoryId", categoryId);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportDateEvolutionData> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(convertDailyResultsToReportDateEvolutionData(res));
        }
        return rdedResults;
    }

    public List<MyaReportMonthlyData> findMonthlyReportDataWhereCategoryBetweenMonth(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        Query querySelect = entityManager.createNativeQuery(getSelectMonthlyReportDataWhereCategoryBetweenMonth());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("categoryId", categoryId);
        querySelect.setParameter("fromDate", fromDate);
        querySelect.setParameter("toDate", toDate);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportMonthlyData> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(new MyaReportMonthlyData(res));
        }
        return rdedResults;
    }

    public List<MyaReportMonthlyData> findMonthlyReportDataBetweenMonth(Long accountId, LocalDate fromDate, LocalDate toDate) {
        Query querySelect = entityManager.createNativeQuery(getSelectMonthlyReportDataBetweenMonth());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("fromDate", fromDate);
        querySelect.setParameter("toDate", toDate);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportMonthlyData> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(new MyaReportMonthlyData(res));
        }
        return rdedResults;
    }

    public List<MyaReportDateEvolutionData> findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        Query querySelect = entityManager.createNativeQuery(getSelectMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("categoryId", categoryId);
        querySelect.setParameter("fromDate", fromDate);
        querySelect.setParameter("toDate", toDate);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportDateEvolutionData> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(
                new MyaReportDateEvolutionData()
                    .month(res[0])
                    .categoryId(res[2])
                    .operationAmount(res[3])
                    .budgetUnSmoothedAtDateAmount(res[4])
                    .budgetSmoothedAmount(res[5])
                    .budgetUnSmoothedMarkedAmount(res[6])
                    .budgetUnSmoothedUnMarkedAmount(res[7])
            );
        }
        return rdedResults;
    }

    public List<MyaCategorySplit> findSubCategorySplit(Long accountId, Long categoryId, LocalDate month, int numberOfMonths) {
        Query querySelect = entityManager.createNativeQuery(getSelectSubCategorySplitQuery());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("categoryId", categoryId);
        querySelect.setParameter("month", month);
        querySelect.setParameter("numberOfMonths", numberOfMonths);
        List<Object[]> results = querySelect.getResultList();
        List<MyaCategorySplit> rdedResults = new ArrayList<>();
        for (Object[] res : results) {
            rdedResults.add(new MyaCategorySplit().categoryName(res[1]).amount(res[2]));
        }
        return rdedResults;
    }

    public List<MyaReportAmountsByDates> findAmountsBetweenDates(Long accountId, LocalDate dateFrom, LocalDate dateTo) {
        Query querySelect = entityManager.createNativeQuery(getSelectAmountsBetweenDatesQuery());
        querySelect.setParameter("accountId", accountId);
        querySelect.setParameter("dateFrom", dateFrom);
        querySelect.setParameter("dateTo", dateTo);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportAmountsByDates> returns = new ArrayList<>();
        for (Object[] res : results) {
            returns.add(new MyaReportAmountsByDates().date(res[0]).amount(res[1]).predictiveAmount(res[2]));
        }
        return returns;
    }

    public List<MyaReportAmountsByDates> findAmountsBetweenDatesForBankAccount(Long bankAccountId) {
        Query querySelect = entityManager.createNativeQuery(getSelectAmountsBetweenDatesForBankAccountQuery());

        querySelect.setParameter("bankAccountId", bankAccountId);
        List<Object[]> results = querySelect.getResultList();
        List<MyaReportAmountsByDates> returns = new ArrayList<>();
        for (Object[] res : results) {
            returns.add(new MyaReportAmountsByDates().date(res[0]).amount(res[1]).predictiveAmount(res[2]));
        }
        return returns;
    }
}
