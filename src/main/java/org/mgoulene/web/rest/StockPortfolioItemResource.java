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
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.service.StockPortfolioItemQueryService;
import org.mgoulene.service.StockPortfolioItemService;
import org.mgoulene.service.criteria.StockPortfolioItemCriteria;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.mgoulene.domain.StockPortfolioItem}.
 */
@RestController
@RequestMapping("/api")
public class StockPortfolioItemResource {

    private final Logger log = LoggerFactory.getLogger(StockPortfolioItemResource.class);

    private static final String ENTITY_NAME = "stockPortfolioItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockPortfolioItemService stockPortfolioItemService;

    private final StockPortfolioItemRepository stockPortfolioItemRepository;

    private final StockPortfolioItemQueryService stockPortfolioItemQueryService;

    public StockPortfolioItemResource(
        StockPortfolioItemService stockPortfolioItemService,
        StockPortfolioItemRepository stockPortfolioItemRepository,
        StockPortfolioItemQueryService stockPortfolioItemQueryService
    ) {
        this.stockPortfolioItemService = stockPortfolioItemService;
        this.stockPortfolioItemRepository = stockPortfolioItemRepository;
        this.stockPortfolioItemQueryService = stockPortfolioItemQueryService;
    }

    /**
     * {@code POST  /stock-portfolio-items} : Create a new stockPortfolioItem.
     *
     * @param stockPortfolioItemDTO the stockPortfolioItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockPortfolioItemDTO, or with status {@code 400 (Bad Request)} if the stockPortfolioItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-portfolio-items")
    public ResponseEntity<StockPortfolioItemDTO> createStockPortfolioItem(@Valid @RequestBody StockPortfolioItemDTO stockPortfolioItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save StockPortfolioItem : {}", stockPortfolioItemDTO);
        if (stockPortfolioItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockPortfolioItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockPortfolioItemDTO result = stockPortfolioItemService.save(stockPortfolioItemDTO);
        return ResponseEntity
            .created(new URI("/api/stock-portfolio-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-portfolio-items/:id} : Updates an existing stockPortfolioItem.
     *
     * @param id the id of the stockPortfolioItemDTO to save.
     * @param stockPortfolioItemDTO the stockPortfolioItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockPortfolioItemDTO,
     * or with status {@code 400 (Bad Request)} if the stockPortfolioItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockPortfolioItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-portfolio-items/{id}")
    public ResponseEntity<StockPortfolioItemDTO> updateStockPortfolioItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockPortfolioItemDTO stockPortfolioItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockPortfolioItem : {}, {}", id, stockPortfolioItemDTO);
        if (stockPortfolioItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockPortfolioItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockPortfolioItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockPortfolioItemDTO result = stockPortfolioItemService.update(stockPortfolioItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockPortfolioItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-portfolio-items/:id} : Partial updates given fields of an existing stockPortfolioItem, field will ignore if it is null
     *
     * @param id the id of the stockPortfolioItemDTO to save.
     * @param stockPortfolioItemDTO the stockPortfolioItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockPortfolioItemDTO,
     * or with status {@code 400 (Bad Request)} if the stockPortfolioItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockPortfolioItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockPortfolioItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-portfolio-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockPortfolioItemDTO> partialUpdateStockPortfolioItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockPortfolioItemDTO stockPortfolioItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockPortfolioItem partially : {}, {}", id, stockPortfolioItemDTO);
        if (stockPortfolioItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockPortfolioItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockPortfolioItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockPortfolioItemDTO> result = stockPortfolioItemService.partialUpdate(stockPortfolioItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockPortfolioItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-portfolio-items} : get all the stockPortfolioItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockPortfolioItems in body.
     */
    @GetMapping("/stock-portfolio-items")
    public ResponseEntity<List<StockPortfolioItemDTO>> getAllStockPortfolioItems(
        StockPortfolioItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockPortfolioItems by criteria: {}", criteria);
        Page<StockPortfolioItemDTO> page = stockPortfolioItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-portfolio-items/count} : count all the stockPortfolioItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-portfolio-items/count")
    public ResponseEntity<Long> countStockPortfolioItems(StockPortfolioItemCriteria criteria) {
        log.debug("REST request to count StockPortfolioItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockPortfolioItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-portfolio-items/:id} : get the "id" stockPortfolioItem.
     *
     * @param id the id of the stockPortfolioItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockPortfolioItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-portfolio-items/{id}")
    public ResponseEntity<StockPortfolioItemDTO> getStockPortfolioItem(@PathVariable Long id) {
        log.debug("REST request to get StockPortfolioItem : {}", id);
        Optional<StockPortfolioItemDTO> stockPortfolioItemDTO = stockPortfolioItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockPortfolioItemDTO);
    }

    /**
     * {@code DELETE  /stock-portfolio-items/:id} : delete the "id" stockPortfolioItem.
     *
     * @param id the id of the stockPortfolioItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-portfolio-items/{id}")
    public ResponseEntity<Void> deleteStockPortfolioItem(@PathVariable Long id) {
        log.debug("REST request to delete StockPortfolioItem : {}", id);
        stockPortfolioItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stock-portfolio-items?query=:query} : search for the stockPortfolioItem corresponding
     * to the query.
     *
     * @param query the query of the stockPortfolioItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/stock-portfolio-items")
    public ResponseEntity<List<StockPortfolioItemDTO>> searchStockPortfolioItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of StockPortfolioItems for query {}", query);
        Page<StockPortfolioItemDTO> page = stockPortfolioItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
