package org.mgoulene.mya.service;

import java.time.LocalDate;
import java.util.List;
import org.mgoulene.mya.domain.MyaCategorySplit;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.domain.MyaReportDateEvolutionData;
import org.mgoulene.mya.domain.MyaReportMonthlyData;
import org.mgoulene.mya.repository.MyaReportDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MyaReportDataService {

    private final Logger log = LoggerFactory.getLogger(MyaReportDataService.class);

    private final MyaReportDataRepository reportDataRepository;

    public MyaReportDataService(MyaReportDataRepository reportDataRepository) {
        this.reportDataRepository = reportDataRepository;
    }

    public List<MyaReportMonthlyData> findMonthlyReportDataBetweenMonth(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        log.debug(
            "Request to get all ReportMonthlyData accountId {}, categoryId {}, fromDate: {}, toDate: {}",
            accountId,
            categoryId,
            fromDate,
            toDate
        );
        if (categoryId != null) {
            return reportDataRepository.findMonthlyReportDataWhereCategoryBetweenMonth(accountId, categoryId, fromDate, toDate);
        } else {
            return reportDataRepository.findMonthlyReportDataBetweenMonth(accountId, fromDate, toDate);
        }
    }

    public List<MyaReportDateEvolutionData> findReportDataWhereMonth(Long accountId, LocalDate month, Long categoryId) {
        log.debug("Request to get all findReportDataWhereMonth accountId {}, month {}, category {}", accountId, month, categoryId);
        if (categoryId == null) {
            return reportDataRepository.findReportDataWhereMonth(accountId, month);
        } else {
            return reportDataRepository.findReportDataWhereMonthAndCategory(accountId, month, categoryId);
        }
    }

    public List<MyaReportDateEvolutionData> findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked(
        Long accountId,
        Long categoryId,
        LocalDate fromDate,
        LocalDate toDate
    ) {
        log.debug(
            "Request to get all findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked accountId {}, categoryId {}, fromDate: {}, toDate: {}",
            accountId,
            categoryId,
            fromDate,
            toDate
        );
        return reportDataRepository.findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked(accountId, categoryId, fromDate, toDate);
    }

    public List<MyaCategorySplit> findSubCategorySplit(Long accountId, Long categoryId, LocalDate month, int numberOfMonths) {
        log.debug(
            "Request to get all findSubCategorySplit accountId {}, categoryId {}, month: {}, numberOfMonths: {}",
            accountId,
            categoryId,
            month,
            numberOfMonths
        );
        return reportDataRepository.findSubCategorySplit(accountId, categoryId, month, numberOfMonths);
    }

    public List<MyaReportAmountsByDates> findAmountsBetweenDates(Long accountId, LocalDate dateFrom, LocalDate dateTo) {
        log.debug("Request to get all findAmountsBetweenDates accountId {}, dateFrom {}, dateTo: {}", accountId, dateFrom, dateTo);
        return reportDataRepository.findAmountsBetweenDates(accountId, dateFrom, dateTo);
    }

    public List<MyaReportAmountsByDates> findAmountsBetweenDatesForBankAccount(Long bankAccountId) {
        log.debug("Request to get all findAmountsBetweenDates bankAccountId {}", bankAccountId);
        return reportDataRepository.findAmountsBetweenDatesForBankAccount(bankAccountId);
    }

    public List<MyaReportAmountsByDates> findUserBankAccountDateDataPoints(Long applicationUserId) {
        log.debug("Request to get all findUserBankAccountDateDataPoints applicationUserId {}", applicationUserId);
        return reportDataRepository.findUserBankAccountDateDataPoints(applicationUserId);
    }

    public List<MyaReportAmountsByDates> findUserCurrentBankAccountDateDataPoints(Long applicationUserId) {
        log.debug("Request to get all findUserCurrentBankAccountDateDataPoints applicationUserId {}", applicationUserId);
        return reportDataRepository.findUserCurrentBankAccountDateDataPoints(applicationUserId);
    }

    public List<MyaReportAmountsByDates> findUserSavingsBankAccountDateDataPoints(Long applicationUserId) {
        log.debug("Request to get all findUserSavingsBankAccountDateDataPoints applicationUserId {}", applicationUserId);
        return reportDataRepository.findUserSavingsBankAccountDateDataPoints(applicationUserId);
    }

    public List<MyaReportAmountsByDates> findBankAccountDateDataPoints(Long bankAccountId) {
        log.debug("Request to get all findBankAccountDateDataPoints bankAccountId {}", bankAccountId);
        return reportDataRepository.findBankAccountDateDataPoints(bankAccountId);
    }

    public List<MyaReportAmountsByDates> findRealEstateBankAccountDateDataPoints(Long bankAccountId) {
        log.debug("Request to get all findRealEstateBankAccountDateDataPoints bankAccountId {}", bankAccountId);
        return reportDataRepository.findRealEstateBankAccountDateDataPoints(bankAccountId);
    }
}
