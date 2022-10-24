package org.mgoulene.mya.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.mgoulene.mya.service.MyaApplicationUserService;
import org.mgoulene.mya.service.MyaBudgetItemPeriodService;
import org.mgoulene.mya.service.MyaBudgetItemService;
import org.mgoulene.service.BudgetItemQueryService;
import org.mgoulene.service.criteria.BudgetItemCriteria;
import org.mgoulene.service.dto.ApplicationUserDTO;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.web.rest.errors.BadRequestAlertException;
import org.mgoulene.web.rest.util.LocalDateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.BudgetItem}.
 */
@RestController
@RequestMapping("/api")
public class MyaBudgetItemResource {

    private final Logger log = LoggerFactory.getLogger(MyaBudgetItemResource.class);

    private static final String ENTITY_NAME = "budgetItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MyaBudgetItemService budgetItemService;

    private final BudgetItemQueryService budgetItemQueryService;

    private final MyaApplicationUserService myaApplicationUserService;

    private final MyaBudgetItemPeriodService myaBudgetItemPeriodService;

    public MyaBudgetItemResource(
        MyaBudgetItemService budgetItemService,
        BudgetItemQueryService budgetItemQueryService,
        MyaApplicationUserService myaApplicationUserService,
        MyaBudgetItemPeriodService myaBudgetItemPeriodService
    ) {
        this.budgetItemService = budgetItemService;
        this.budgetItemQueryService = budgetItemQueryService;
        this.myaApplicationUserService = myaApplicationUserService;
        this.myaBudgetItemPeriodService = myaBudgetItemPeriodService;
    }

    /**
     * {@code GET  /budget-items} : get all the budgetItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of budgetItems in body.
     */
    @GetMapping("/mya/budget-items/with-signedin-user")
    public ResponseEntity<List<BudgetItemDTO>> getAllBudgetItemsWithUser(BudgetItemCriteria criteria) {
        log.debug("REST request to get BudgetItems with Application Userby criteria: {}", criteria.toString().replaceAll("[\n\r\t]", "_"));
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            LongFilter userFilter = new LongFilter();
            userFilter.setEquals(applicationUserOptional.get().getId());
            criteria.setAccountId(userFilter);

            List<BudgetItemDTO> entityList = budgetItemQueryService.findByCriteria(criteria);
            return ResponseEntity.ok().body(entityList);
        }
        return null;
    }

    @GetMapping(
        value = {
            "/mya-budget-items/with-signedin-user/between/{from}/{to}/{categoryId}",
            "/mya-budget-items/with-signedin-user/between/{from}/{to}",
        }
    )
    public ResponseEntity<List<BudgetItemDTO>> getAllBudgetItemsWithUserBetween(
        @PathVariable(name = "from") LocalDate from,
        @PathVariable(name = "to") LocalDate to,
        @PathVariable(name = "categoryId") Optional<Long> categoryId
    ) {
        log.debug("REST request to get BudgetItems between {} and {}, with categoryId {}", from, to, categoryId);
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            List<BudgetItemDTO> entityList = budgetItemService.findAllAvailableBetweenDate(
                applicationUserOptional.get().getId(),
                from,
                to,
                categoryId.isPresent() ? categoryId.get() : null
            );
            return ResponseEntity.ok().body(entityList);
        }
        return null;
    }

    /**
     * {@code SEARCH  /_search/budget-items?query=:query} : search for the
     * budgetItem corresponding
     * to the query.
     *
     * @param query the query of the budgetItem search.
     * @return the result of the search.
     */
    @GetMapping("/_search/mya-budget-items/with-signedin-user")
    public List<BudgetItemDTO> searchBudgetItemsWithUser(@RequestParam String query) {
        log.debug("REST request to search BudgetItems for query {}", query);
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            query += " AND account.id:" + applicationUserOptional.get().getId();
            return budgetItemService.search(query);
        }
        return null;
    }

    /**
     * DELETE /budget-items-with-periods/:id : delete the "id" budgetItem with the
     * budgetItemPeriod.
     *
     * @param id the id of the budgetItemDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/mya-budget-items/with-periods/{id}")
    public ResponseEntity<Void> deleteBudgetItemWithPeriods(@PathVariable Long id) {
        log.debug("REST request to delete with Periods BudgetItem : {}", id);
        myaBudgetItemPeriodService.deleteFromBudgetItem(id);
        budgetItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    @GetMapping("/mya-budget-items/reorder/up/{fromId}/{toId}")
    public ResponseEntity<Void> reorderUp(@PathVariable(name = "fromId") Long fromId, @PathVariable(name = "toId") Long toId) {
        log.debug("REST request to reorder up {} with {}", fromId, toId);
        budgetItemService.reorderUp(fromId, toId);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/mya-budget-items/reorder/down/{fromId}/{toId}")
    public ResponseEntity<Void> reorderDown(@PathVariable(name = "fromId") Long fromId, @PathVariable(name = "toId") Long toId) {
        log.debug("REST request to reorder down {} with {}", fromId, toId);
        budgetItemService.reorderDown(fromId, toId);
        return null;
    }

    @PostMapping("/mya-budget-items/with-periods/{is-smoothed}/{monthFrom}/{amount}/{day-in-month}")
    public ResponseEntity<BudgetItemDTO> createBudgetItemWithPeriods(
        @RequestBody BudgetItemDTO budgetItemDTO,
        @PathVariable(name = "monthFrom") LocalDate monthFrom,
        @PathVariable(name = "day-in-month") Integer dayInMonth,
        @PathVariable(name = "is-smoothed") Boolean isSmoothed,
        @PathVariable(name = "amount") Float amount
    ) throws URISyntaxException {
        log.debug("REST request to save BudgetItem with period : {} {} {} {}", budgetItemDTO, monthFrom, isSmoothed, amount);
        if (budgetItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new budgetItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Optional<ApplicationUserDTO> applicationUserOptional = myaApplicationUserService.findSignedInApplicationUser();
        if (applicationUserOptional.isPresent()) {
            budgetItemDTO.setAccount(applicationUserOptional.get());
        }

        BudgetItemPeriodDTO budgetItemPeriodDTO = new BudgetItemPeriodDTO();
        budgetItemPeriodDTO.setMonth(monthFrom);
        budgetItemPeriodDTO.setAmount(amount);
        budgetItemPeriodDTO.setIsSmoothed(isSmoothed);
        budgetItemPeriodDTO.setIsRecurrent(true);
        if (dayInMonth != null && !isSmoothed) {
            budgetItemPeriodDTO.setDate(LocalDateUtil.getLocalDate(monthFrom, dayInMonth));
        }
        budgetItemDTO.setOrder(budgetItemService.findNewOrder(budgetItemDTO.getAccount().getId()));
        BudgetItemDTO result = budgetItemService.saveWithBudgetItemPeriod(budgetItemDTO, budgetItemPeriodDTO);
        return ResponseEntity
            .created(new URI("/api/mya-budget-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
