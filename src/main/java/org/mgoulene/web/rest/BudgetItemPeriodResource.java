package org.mgoulene.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.mgoulene.repository.BudgetItemPeriodRepository;
import org.mgoulene.service.BudgetItemPeriodQueryService;
import org.mgoulene.service.BudgetItemPeriodService;
import org.mgoulene.service.criteria.BudgetItemPeriodCriteria;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.BudgetItemPeriod}.
 */
@RestController
@RequestMapping("/api")
public class BudgetItemPeriodResource {

    private final Logger log = LoggerFactory.getLogger(BudgetItemPeriodResource.class);

    private static final String ENTITY_NAME = "budgetItemPeriod";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BudgetItemPeriodService budgetItemPeriodService;

    private final BudgetItemPeriodRepository budgetItemPeriodRepository;

    private final BudgetItemPeriodQueryService budgetItemPeriodQueryService;

    public BudgetItemPeriodResource(
        BudgetItemPeriodService budgetItemPeriodService,
        BudgetItemPeriodRepository budgetItemPeriodRepository,
        BudgetItemPeriodQueryService budgetItemPeriodQueryService
    ) {
        this.budgetItemPeriodService = budgetItemPeriodService;
        this.budgetItemPeriodRepository = budgetItemPeriodRepository;
        this.budgetItemPeriodQueryService = budgetItemPeriodQueryService;
    }

    /**
     * {@code POST  /budget-item-periods} : Create a new budgetItemPeriod.
     *
     * @param budgetItemPeriodDTO the budgetItemPeriodDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new budgetItemPeriodDTO, or with status {@code 400 (Bad Request)} if the budgetItemPeriod has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/budget-item-periods")
    public ResponseEntity<BudgetItemPeriodDTO> createBudgetItemPeriod(@Valid @RequestBody BudgetItemPeriodDTO budgetItemPeriodDTO)
        throws URISyntaxException {
        log.debug("REST request to save BudgetItemPeriod : {}", budgetItemPeriodDTO);
        if (budgetItemPeriodDTO.getId() != null) {
            throw new BadRequestAlertException("A new budgetItemPeriod cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BudgetItemPeriodDTO result = budgetItemPeriodService.save(budgetItemPeriodDTO);
        return ResponseEntity
            .created(new URI("/api/budget-item-periods/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /budget-item-periods/:id} : Updates an existing budgetItemPeriod.
     *
     * @param id the id of the budgetItemPeriodDTO to save.
     * @param budgetItemPeriodDTO the budgetItemPeriodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated budgetItemPeriodDTO,
     * or with status {@code 400 (Bad Request)} if the budgetItemPeriodDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the budgetItemPeriodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/budget-item-periods/{id}")
    public ResponseEntity<BudgetItemPeriodDTO> updateBudgetItemPeriod(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BudgetItemPeriodDTO budgetItemPeriodDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BudgetItemPeriod : {}, {}", id, budgetItemPeriodDTO);
        if (budgetItemPeriodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, budgetItemPeriodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!budgetItemPeriodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BudgetItemPeriodDTO result = budgetItemPeriodService.update(budgetItemPeriodDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, budgetItemPeriodDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /budget-item-periods/:id} : Partial updates given fields of an existing budgetItemPeriod, field will ignore if it is null
     *
     * @param id the id of the budgetItemPeriodDTO to save.
     * @param budgetItemPeriodDTO the budgetItemPeriodDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated budgetItemPeriodDTO,
     * or with status {@code 400 (Bad Request)} if the budgetItemPeriodDTO is not valid,
     * or with status {@code 404 (Not Found)} if the budgetItemPeriodDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the budgetItemPeriodDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/budget-item-periods/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BudgetItemPeriodDTO> partialUpdateBudgetItemPeriod(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BudgetItemPeriodDTO budgetItemPeriodDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BudgetItemPeriod partially : {}, {}", id, budgetItemPeriodDTO);
        if (budgetItemPeriodDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, budgetItemPeriodDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!budgetItemPeriodRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BudgetItemPeriodDTO> result = budgetItemPeriodService.partialUpdate(budgetItemPeriodDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, budgetItemPeriodDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /budget-item-periods} : get all the budgetItemPeriods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of budgetItemPeriods in body.
     */
    @GetMapping("/budget-item-periods")
    public ResponseEntity<List<BudgetItemPeriodDTO>> getAllBudgetItemPeriods(BudgetItemPeriodCriteria criteria) {
        log.debug("REST request to get BudgetItemPeriods by criteria: {}", criteria);
        List<BudgetItemPeriodDTO> entityList = budgetItemPeriodQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /budget-item-periods/count} : count all the budgetItemPeriods.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/budget-item-periods/count")
    public ResponseEntity<Long> countBudgetItemPeriods(BudgetItemPeriodCriteria criteria) {
        log.debug("REST request to count BudgetItemPeriods by criteria: {}", criteria);
        return ResponseEntity.ok().body(budgetItemPeriodQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /budget-item-periods/:id} : get the "id" budgetItemPeriod.
     *
     * @param id the id of the budgetItemPeriodDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the budgetItemPeriodDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/budget-item-periods/{id}")
    public ResponseEntity<BudgetItemPeriodDTO> getBudgetItemPeriod(@PathVariable Long id) {
        log.debug("REST request to get BudgetItemPeriod : {}", id);
        Optional<BudgetItemPeriodDTO> budgetItemPeriodDTO = budgetItemPeriodService.findOne(id);
        return ResponseUtil.wrapOrNotFound(budgetItemPeriodDTO);
    }

    /**
     * {@code DELETE  /budget-item-periods/:id} : delete the "id" budgetItemPeriod.
     *
     * @param id the id of the budgetItemPeriodDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/budget-item-periods/{id}")
    public ResponseEntity<Void> deleteBudgetItemPeriod(@PathVariable Long id) {
        log.debug("REST request to delete BudgetItemPeriod : {}", id);
        budgetItemPeriodService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/budget-item-periods?query=:query} : search for the budgetItemPeriod corresponding
     * to the query.
     *
     * @param query the query of the budgetItemPeriod search.
     * @return the result of the search.
     */
    @GetMapping("/_search/budget-item-periods")
    public List<BudgetItemPeriodDTO> searchBudgetItemPeriods(@RequestParam String query) {
        log.debug("REST request to search BudgetItemPeriods for query {}", query);
        return budgetItemPeriodService.search(query);
    }
}
