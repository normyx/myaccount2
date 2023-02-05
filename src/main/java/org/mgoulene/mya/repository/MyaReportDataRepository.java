package org.mgoulene.mya.repository;

import java.time.LocalDate;
import java.util.List;
import org.mgoulene.mya.domain.MyaCategorySplit;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.domain.MyaReportDateEvolutionData;
import org.mgoulene.mya.domain.MyaReportMonthlyData;

public interface MyaReportDataRepository {
    public List<MyaReportDateEvolutionData> findReportDataWhereMonth(Long accountId, LocalDate month);

    public List<MyaReportDateEvolutionData> findReportDataWhereMonthAndCategory(Long accountId, LocalDate month, Long categoryId);

    public List<MyaReportMonthlyData> findMonthlyReportDataWhereCategoryBetweenMonth(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    );

    public List<MyaReportMonthlyData> findMonthlyReportDataBetweenMonth(Long accountId, LocalDate fromDate, LocalDate toDate);

    public List<MyaReportDateEvolutionData> findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    );

    public List<MyaCategorySplit> findSubCategorySplit(Long accountId, Long categoryId, LocalDate month, int numberOfMonths);

    public List<MyaReportAmountsByDates> findAmountsBetweenDates(Long accountId, LocalDate dateFrom, LocalDate dateTo);

    public List<MyaReportAmountsByDates> findAmountsBetweenDatesForBankAccount(Long bankAccountId);

    public List<MyaReportAmountsByDates> findUserBankAccountDateDataPoints(Long applicationUserId);

    public List<MyaReportAmountsByDates> findUserCurrentBankAccountDateDataPoints(Long applicationUserId);

    public List<MyaReportAmountsByDates> findUserSavingsBankAccountDateDataPoints(Long applicationUserId);

    public List<MyaReportAmountsByDates> findBankAccountDateDataPoints(Long bankAccountId);

    public List<MyaReportAmountsByDates> findRealEstateBankAccountDateDataPoints(Long bankAccountId);
}
