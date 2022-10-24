package org.mgoulene.mya.web.rest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.mgoulene.mya.service.MyaBudgetItemPeriodService;
import org.mgoulene.service.BudgetItemPeriodQueryService;
import org.mgoulene.service.BudgetItemPeriodService;
import org.mgoulene.service.criteria.BudgetItemPeriodCriteria;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.web.rest.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LocalDateFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.BudgetItemPeriod}.
 */
@RestController
@RequestMapping("/api")
public class MyaBudgetItemPeriodResource {

    private final Logger log = LoggerFactory.getLogger(MyaBudgetItemPeriodResource.class);

    private static final String ENTITY_NAME = "budgetItemPeriod";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BudgetItemPeriodQueryService budgetItemPeriodQueryService;
    private final MyaBudgetItemPeriodService budgetItemPeriodService;

    public MyaBudgetItemPeriodResource(
        BudgetItemPeriodQueryService budgetItemPeriodQueryService,
        MyaBudgetItemPeriodService budgetItemPeriodService
    ) {
        this.budgetItemPeriodQueryService = budgetItemPeriodQueryService;
        this.budgetItemPeriodService = budgetItemPeriodService;
    }

    /**
     * {@code GET  /budget-item-periods} : get all the budgetItemPeriods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of budgetItemPeriods in body.
     */
    @GetMapping("/mya-budget-item-periods/{budgetItemId}/{from}/{to}")
    public ResponseEntity<List<BudgetItemPeriodDTO>> getBudgetItemPeriodsBelongToBudgetItemFromToDate(
        @PathVariable("budgetItemId") Long budgetItemId,
        @PathVariable("from") LocalDate from,
        @PathVariable("to") LocalDate to
    ) {
        log.debug("REST request to get BudgetItemPeriods belong to BudgetItem {}, from {} to {}", budgetItemId, from, to);
        BudgetItemPeriodCriteria criteria = new BudgetItemPeriodCriteria();
        LongFilter budgetItemIdFilter = new LongFilter();
        budgetItemIdFilter.setEquals(budgetItemId);
        LocalDateFilter dateFilter = new LocalDateFilter();
        dateFilter.setGreaterThanOrEqual(from);
        dateFilter.setLessThan(to);
        criteria.setBudgetItemId(budgetItemIdFilter);
        criteria.setMonth(dateFilter);
        List<BudgetItemPeriodDTO> entityList = budgetItemPeriodQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    @PutMapping("/mya-budget-item-periods/with-nexts/{day}/{next}")
    public ResponseEntity<Void> updateBudgetItemPeriodAndNext(
        @Valid @RequestBody BudgetItemPeriodDTO budgetItemPeriodDTO,
        @PathVariable(name = "day") String dayStr,
        @PathVariable(name = "next") boolean withNext
    ) {
        int day = 0;
        if (!dayStr.equals("null")) {
            day = Integer.parseInt(dayStr);
        }
        log.debug("REST request to update updateBudgetItemPeriodAndNext and next : {}, {}, {}", budgetItemPeriodDTO, day, withNext);
        // Modifify the first one
        if (!budgetItemPeriodDTO.getIsSmoothed()) {
            budgetItemPeriodDTO.setDate(LocalDateUtil.getLocalDate(budgetItemPeriodDTO.getMonth(), day));
        }
        budgetItemPeriodService.save(budgetItemPeriodDTO);
        // Gets all BudgetPeriodAndNext
        if (budgetItemPeriodDTO.getIsRecurrent()) {
            BudgetItemPeriodCriteria criteria = new BudgetItemPeriodCriteria();
            // Filter on budget ID
            LongFilter biIdF = new LongFilter();
            biIdF.setEquals(budgetItemPeriodDTO.getBudgetItem().getId());
            criteria.setBudgetItemId(biIdF);
            // Filter for date greater that date
            LocalDateFilter biMonthF = new LocalDateFilter();
            // Find next month
            biMonthF.setGreaterThanOrEqual(budgetItemPeriodDTO.getMonth().plusMonths(1));
            if (!withNext) {
                biMonthF.setLessThanOrEqual(budgetItemPeriodDTO.getMonth());
            }
            criteria.setMonth(biMonthF);
            LongFilter opFilter = new LongFilter();
            opFilter.setSpecified(false);
            criteria.setOperationId(opFilter);
            // Filter on recurrent period only
            BooleanFilter isRecurrentF = new BooleanFilter();
            isRecurrentF.setEquals(true);
            criteria.setIsRecurrent(isRecurrentF);
            List<BudgetItemPeriodDTO> allBudgetItemPeriodsfromMonth = budgetItemPeriodQueryService.findByCriteria(criteria);
            for (BudgetItemPeriodDTO bip : allBudgetItemPeriodsfromMonth) {
                bip.setAmount(budgetItemPeriodDTO.getAmount());
                bip.setIsSmoothed(budgetItemPeriodDTO.getIsSmoothed());
                if (!bip.getIsSmoothed()) {
                    bip.setDate(LocalDateUtil.getLocalDate(bip.getMonth(), day));
                }
            }
            budgetItemPeriodService.save(allBudgetItemPeriodsfromMonth);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /budget-item-periods/:id : delete the "id" budgetItemPeriod.
     *
     * @param id the id of the budgetItemPeriodDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/mya-budget-item-periods/with-nexts/{id}")
    public ResponseEntity<Void> deleteBudgetItemPeriodAndNext(@PathVariable Long id) {
        log.debug("REST request to delete BudgetItemPeriod : {}", id);
        Optional<BudgetItemPeriodDTO> budgetItemPeriodDTOOptional = budgetItemPeriodService.findOne(id);
        if (budgetItemPeriodDTOOptional.isPresent()) {
            BudgetItemPeriodDTO budgetItemPeriodDTO = budgetItemPeriodDTOOptional.get();
            if (budgetItemPeriodDTO.getIsRecurrent()) {
                budgetItemPeriodService.deleteWithNext(budgetItemPeriodDTO);
            } else {
                budgetItemPeriodService.delete(budgetItemPeriodDTO.getId());
            }
        }
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
