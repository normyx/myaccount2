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
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.service.BudgetItemQueryService;
import org.mgoulene.service.BudgetItemService;
import org.mgoulene.service.criteria.BudgetItemCriteria;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.BudgetItem}.
 */
@RestController
@RequestMapping("/api")
public class BudgetItemResource {

    private final Logger log = LoggerFactory.getLogger(BudgetItemResource.class);

    private static final String ENTITY_NAME = "budgetItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BudgetItemService budgetItemService;

    private final BudgetItemRepository budgetItemRepository;

    private final BudgetItemQueryService budgetItemQueryService;

    public BudgetItemResource(
        BudgetItemService budgetItemService,
        BudgetItemRepository budgetItemRepository,
        BudgetItemQueryService budgetItemQueryService
    ) {
        this.budgetItemService = budgetItemService;
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemQueryService = budgetItemQueryService;
    }

    /**
     * {@code POST  /budget-items} : Create a new budgetItem.
     *
     * @param budgetItemDTO the budgetItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new budgetItemDTO, or with status {@code 400 (Bad Request)} if the budgetItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/budget-items")
    public ResponseEntity<BudgetItemDTO> createBudgetItem(@Valid @RequestBody BudgetItemDTO budgetItemDTO) throws URISyntaxException {
        log.debug("REST request to save BudgetItem : {}", budgetItemDTO);
        if (budgetItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new budgetItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        BudgetItemDTO result = budgetItemService.save(budgetItemDTO);
        return ResponseEntity
            .created(new URI("/api/budget-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /budget-items/:id} : Updates an existing budgetItem.
     *
     * @param id the id of the budgetItemDTO to save.
     * @param budgetItemDTO the budgetItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated budgetItemDTO,
     * or with status {@code 400 (Bad Request)} if the budgetItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the budgetItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/budget-items/{id}")
    public ResponseEntity<BudgetItemDTO> updateBudgetItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BudgetItemDTO budgetItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update BudgetItem : {}, {}", id, budgetItemDTO);
        if (budgetItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, budgetItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!budgetItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        BudgetItemDTO result = budgetItemService.update(budgetItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, budgetItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /budget-items/:id} : Partial updates given fields of an existing budgetItem, field will ignore if it is null
     *
     * @param id the id of the budgetItemDTO to save.
     * @param budgetItemDTO the budgetItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated budgetItemDTO,
     * or with status {@code 400 (Bad Request)} if the budgetItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the budgetItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the budgetItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/budget-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BudgetItemDTO> partialUpdateBudgetItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BudgetItemDTO budgetItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update BudgetItem partially : {}, {}", id, budgetItemDTO);
        if (budgetItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, budgetItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!budgetItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BudgetItemDTO> result = budgetItemService.partialUpdate(budgetItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, budgetItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /budget-items} : get all the budgetItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of budgetItems in body.
     */
    @GetMapping("/budget-items")
    public ResponseEntity<List<BudgetItemDTO>> getAllBudgetItems(BudgetItemCriteria criteria) {
        log.debug("REST request to get BudgetItems by criteria: {}", criteria);
        List<BudgetItemDTO> entityList = budgetItemQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /budget-items/count} : count all the budgetItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/budget-items/count")
    public ResponseEntity<Long> countBudgetItems(BudgetItemCriteria criteria) {
        log.debug("REST request to count BudgetItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(budgetItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /budget-items/:id} : get the "id" budgetItem.
     *
     * @param id the id of the budgetItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the budgetItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/budget-items/{id}")
    public ResponseEntity<BudgetItemDTO> getBudgetItem(@PathVariable Long id) {
        log.debug("REST request to get BudgetItem : {}", id);
        Optional<BudgetItemDTO> budgetItemDTO = budgetItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(budgetItemDTO);
    }

    /**
     * {@code DELETE  /budget-items/:id} : delete the "id" budgetItem.
     *
     * @param id the id of the budgetItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/budget-items/{id}")
    public ResponseEntity<Void> deleteBudgetItem(@PathVariable Long id) {
        log.debug("REST request to delete BudgetItem : {}", id);
        budgetItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/budget-items?query=:query} : search for the budgetItem corresponding
     * to the query.
     *
     * @param query the query of the budgetItem search.
     * @return the result of the search.
     */
    @GetMapping("/_search/budget-items")
    public List<BudgetItemDTO> searchBudgetItems(@RequestParam String query) {
        log.debug("REST request to search BudgetItems for query {}", query);
        return budgetItemService.search(query);
    }
}
