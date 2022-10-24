package org.mgoulene.mya.web.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mgoulene.mya.domain.MyaCategorySplit;
import org.mgoulene.mya.domain.MyaReportAmountsByDates;
import org.mgoulene.mya.domain.MyaReportDateEvolutionData;
import org.mgoulene.mya.domain.MyaReportMonthlyData;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaOperationService;
import org.mgoulene.mya.service.MyaReportDataService;
import org.mgoulene.mya.web.rest.vm.MyaAccountMonthReportData;
import org.mgoulene.mya.web.rest.vm.MyaReportAmountsByDatesVM;
import org.mgoulene.mya.web.rest.vm.MyaReportCategorySplit;
import org.mgoulene.mya.web.rest.vm.MyaReportDataMonthly;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyaReportDataResource {

    private final Logger log = LoggerFactory.getLogger(MyaReportDataResource.class);
    private final MyaReportDataService reportDataService;
    private final MyaApplicationUserService applicationUserService;
    private final MyaOperationService operationService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public MyaReportDataResource(
        MyaReportDataService reportDataService,
        MyaApplicationUserService applicationUserService,
        MyaOperationService operationService
    ) {
        this.reportDataService = reportDataService;
        this.applicationUserService = applicationUserService;
        this.operationService = operationService;
    }

    /**
     * GET /budget-items : get all the budgetItems.
     *
     * @param categoryId the categoryID which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of budgetItems
     *         in body
     */
    @GetMapping(value = { "/mya-reports/evolution-in-month/{month}", "/mya-reports/evolution-in-month/{month}/{categoryId}" })
    public ResponseEntity<MyaReportDataMonthly> findReportDataByDateWhereAccountIdMonth(
        @PathVariable(name = "month") LocalDate month,
        @PathVariable(name = "categoryId") Optional<Long> categoryIdOpt
    ) {
        log.debug("REST request to get ReportDataResource from month: {}", month);
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            Long categoryId = categoryIdOpt.isPresent() ? categoryIdOpt.get() : null;
            List<MyaReportDateEvolutionData> data = reportDataService.findReportDataWhereMonth(accountId, month, categoryId);
            MyaReportDataMonthly reportDataMonthly = new MyaReportDataMonthly(null, month);
            float cumulOperationAmount = 0;
            float cumulBudgetAmount = 0;
            for (int i = 0; i < data.size(); i++) {
                MyaReportDateEvolutionData rd = data.get(i);
                float operationAmount = rd.getOperationAmount() == null ? 0 : rd.getOperationAmount();
                float budgetSAmount = rd.getBudgetSmoothedAmount() == null ? 0 : rd.getBudgetSmoothedAmount();
                float budgetUSUMAmount = rd.getbudgetUnSmoothedUnMarkedAmount() == null ? 0 : rd.getbudgetUnSmoothedUnMarkedAmount();
                float budgetUSMAmount = rd.getbudgetUnSmoothedMarkedAmount() == null ? 0 : rd.getbudgetUnSmoothedMarkedAmount();
                cumulBudgetAmount += budgetSAmount + budgetUSUMAmount + budgetUSMAmount;
                reportDataMonthly.addDate(rd.getDate()).addBudgetAmount(cumulBudgetAmount);
                if (rd.isHasOperation()) {
                    cumulOperationAmount += operationAmount;
                    reportDataMonthly
                        .addOperationAmounts(cumulOperationAmount)
                        .addPredictiveBudgetAmount(i < data.size() - 1 && !data.get(i + 1).isHasOperation() ? cumulOperationAmount : null);
                } else {
                    cumulOperationAmount += budgetSAmount + budgetUSUMAmount + budgetUSMAmount;
                    reportDataMonthly.addOperationAmounts(null).addPredictiveBudgetAmount(cumulOperationAmount);
                }
            }
            return ResponseEntity.ok().body(reportDataMonthly);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(
        value = {
            "/mya-reports/evolution-by-month/{categoryId}/{monthFrom}/{monthTo}", "/mya-reports/evolution-by-month/{monthFrom}/{monthTo}",
        }
    )
    public ResponseEntity<MyaAccountMonthReportData> findAllFromCategory(
        @PathVariable(name = "categoryId") Optional<Long> categoryIdOpt,
        @PathVariable(name = "monthFrom") LocalDate monthFrom,
        @PathVariable(name = "monthTo") LocalDate monthTo
    ) {
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            Long categoryId = categoryIdOpt.isPresent() ? categoryIdOpt.get() : null;
            log.debug("REST request to get AccountMonthReport from categoryId: {}", categoryId);
            List<MyaReportMonthlyData> entityList = reportDataService.findMonthlyReportDataBetweenMonth(
                accountId,
                categoryId,
                monthFrom,
                monthTo
            );
            MyaAccountMonthReportData data = null;
            if (!entityList.isEmpty()) {
                MyaReportMonthlyData first = entityList.get(0);
                data = new MyaAccountMonthReportData(first.getAccountId(), first.getCategoryId(), first.getCategoryName());
                for (MyaReportMonthlyData report : entityList) {
                    data
                        .addMonth(report.getMonth())
                        .addAmount(report.getAmount())
                        .addAmountAvg3(report.getAmountAvg3())
                        .addAmountAvg12(report.getAmountAvg12())
                        .addBudgetAmount(report.getBudgetAmount());
                }
            }
            return ResponseEntity.ok().body(data);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/mya-reports/category-amount-per-month-with-marked/{categoryId}/{monthFrom}/{monthTo}")
    public ResponseEntity<MyaReportDataMonthly> findReportPerMonthWithCategoryWithMarked(
        @PathVariable(name = "categoryId") Long categoryId,
        @PathVariable(name = "monthFrom") LocalDate monthFrom,
        @PathVariable(name = "monthTo") LocalDate monthTo
    ) {
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            log.debug("REST request to get AccountMonthReport from categoryId: {}", categoryId);
            List<MyaReportDateEvolutionData> entityList = reportDataService.findMonthlyReportDataWhereCategoryBetweenMonthWithUnmarked(
                accountId,
                categoryId,
                monthFrom,
                monthTo
            );
            MyaReportDataMonthly data = null;
            LocalDate last = operationService.findLastOperationDate(accountId);
            if (!entityList.isEmpty()) {
                MyaReportDateEvolutionData first = entityList.get(0);
                data = new MyaReportDataMonthly(first.getCategoryId());
                for (MyaReportDateEvolutionData report : entityList) {
                    float budgetUnSmoothedUnMarkedAmount = report.getbudgetUnSmoothedUnMarkedAmount() != null
                        ? report.getbudgetUnSmoothedUnMarkedAmount()
                        : 0;
                    float budgetUnSmoothedMarkedAmount = report.getbudgetUnSmoothedMarkedAmount() != null
                        ? report.getbudgetUnSmoothedMarkedAmount()
                        : 0;
                    float budgetSmoothedAmount = report.getBudgetSmoothedAmount() != null ? report.getBudgetSmoothedAmount() : 0;
                    // If it is the same month
                    LocalDate month = report.getMonth();
                    float budgetAtDate = report.getBudgetUnSmoothedAtDateAmount() != null ? report.getBudgetUnSmoothedAtDateAmount() : 0;
                    if (month.getYear() == last.getYear() && month.getMonthValue() == last.getMonthValue()) {
                        budgetAtDate += budgetSmoothedAmount * last.getDayOfMonth() / last.lengthOfMonth();
                    } else if (month.isBefore(last)) {
                        budgetAtDate += budgetSmoothedAmount;
                    }
                    data
                        .addMonth(report.getMonth())
                        .addBudgetSmoothedAmounts(budgetUnSmoothedUnMarkedAmount + budgetUnSmoothedMarkedAmount + budgetSmoothedAmount)
                        .addBudgetUnSmoothedMarkedAmounts(budgetUnSmoothedMarkedAmount)
                        .addBudgetUnSmoothedUnMarkedAmounts(budgetUnSmoothedMarkedAmount + budgetUnSmoothedUnMarkedAmount)
                        .addBudgetAtDateAmounts(budgetAtDate)
                        .addOperationAmounts(report.getOperationAmount());
                }
            }
            return ResponseEntity.ok().body(data);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/mya-reports/sub-category-split/{categoryId}/{month}/{numberOfMonths}")
    public ResponseEntity<MyaReportCategorySplit> findSubCategorySplit(
        @PathVariable(name = "categoryId") Long categoryId,
        @PathVariable(name = "month") LocalDate month,
        @PathVariable(name = "numberOfMonths") int numberOfMonths
    ) {
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            log.debug(
                "REST request to get findSubCategorySplit from categoryId: {}, month: {}, numberOfMonths: {}",
                categoryId,
                month,
                numberOfMonths
            );
            List<MyaCategorySplit> entityList = reportDataService.findSubCategorySplit(accountId, categoryId, month, numberOfMonths);
            MyaReportCategorySplit data = new MyaReportCategorySplit();

            for (MyaCategorySplit report : entityList) {
                data.addAmount(report.getAmount()).addCategoryName(report.getCategoryName());
            }

            return ResponseEntity.ok().body(data);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /*@GetMapping("/mya-reports/evolution-between-dates/{dateFrom}/{dateTo}")
    public ResponseEntity<MyaReportAmountsByDatesVM> findAmountsBetweenDates(
            @PathVariable(name = "dateFrom") LocalDate dateFrom,
            @PathVariable(name = "dateTo") LocalDate dateTo) {
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            log.debug("REST request to get findAmountsBetweenDates from dateFrom: {}, dateTo: {}", dateFrom, dateTo);
            List<MyaReportAmountsByDates> entityList = reportDataService.findAmountsBetweenDates(accountId, dateFrom,
                    dateTo);
            MyaReportAmountsByDatesVM data = new MyaReportAmountsByDatesVM();

            for (MyaReportAmountsByDates report : entityList) {
                data.addDate(report.getDate()).addAmount(report.getAmount())
                        .addPredictiveAmounts(report.getPredictiveAmount());
            }

            return ResponseEntity.ok().body(data);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }*/

    @GetMapping("/mya-reports/evolution-between-dates/{dateFrom}/{dateTo}/{bankAccountId}")
    public ResponseEntity<MyaReportAmountsByDatesVM> findAmountsForBankAccountBetweenDates(
        @PathVariable(name = "dateFrom") LocalDate dateFrom,
        @PathVariable(name = "dateTo") LocalDate dateTo,
        @PathVariable(name = "bankAccountId") String bankAccountIdStr
    ) {
        Long bankAccountId = null;
        if (!bankAccountIdStr.equals("null")) {
            bankAccountId = Long.parseLong(bankAccountIdStr);
        }
        Optional<ApplicationUserDTO> applicationUserOptional = applicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            Long accountId = applicationUserOptional.get().getId();
            log.debug("REST request to get findAmountsBetweenDates from dateFrom: {}, dateTo: {}", dateFrom, dateTo);
            List<MyaReportAmountsByDates> entityList;
            if (bankAccountId == null) {
                entityList = reportDataService.findAmountsBetweenDates(accountId, dateFrom, dateTo);
            } else {
                entityList = reportDataService.findAmountsBetweenDatesForBankAccount(bankAccountId);
            }
            MyaReportAmountsByDatesVM data = new MyaReportAmountsByDatesVM();
            for (MyaReportAmountsByDates report : entityList) {
                data.addDate(report.getDate()).addAmount(report.getAmount()).addPredictiveAmounts(report.getPredictiveAmount());
            }

            return ResponseEntity.ok().body(data);
        } else {
            log.error("REST request error to get User");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
