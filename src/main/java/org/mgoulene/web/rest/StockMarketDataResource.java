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
import org.mgoulene.repository.StockMarketDataRepository;
import org.mgoulene.service.StockMarketDataQueryService;
import org.mgoulene.service.StockMarketDataService;
import org.mgoulene.service.criteria.StockMarketDataCriteria;
import org.mgoulene.service.dto.StockMarketDataDTO;
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
 * REST controller for managing {@link org.mgoulene.domain.StockMarketData}.
 */
@RestController
@RequestMapping("/api")
public class StockMarketDataResource {

    private final Logger log = LoggerFactory.getLogger(StockMarketDataResource.class);

    private static final String ENTITY_NAME = "stockMarketData";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockMarketDataService stockMarketDataService;

    private final StockMarketDataRepository stockMarketDataRepository;

    private final StockMarketDataQueryService stockMarketDataQueryService;

    public StockMarketDataResource(
        StockMarketDataService stockMarketDataService,
        StockMarketDataRepository stockMarketDataRepository,
        StockMarketDataQueryService stockMarketDataQueryService
    ) {
        this.stockMarketDataService = stockMarketDataService;
        this.stockMarketDataRepository = stockMarketDataRepository;
        this.stockMarketDataQueryService = stockMarketDataQueryService;
    }

    /**
     * {@code POST  /stock-market-data} : Create a new stockMarketData.
     *
     * @param stockMarketDataDTO the stockMarketDataDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockMarketDataDTO, or with status {@code 400 (Bad Request)} if the stockMarketData has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/stock-market-data")
    public ResponseEntity<StockMarketDataDTO> createStockMarketData(@Valid @RequestBody StockMarketDataDTO stockMarketDataDTO)
        throws URISyntaxException {
        log.debug("REST request to save StockMarketData : {}", stockMarketDataDTO);
        if (stockMarketDataDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockMarketData cannot already have an ID", ENTITY_NAME, "idexists");
        }
        StockMarketDataDTO result = stockMarketDataService.save(stockMarketDataDTO);
        return ResponseEntity
            .created(new URI("/api/stock-market-data/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /stock-market-data/:id} : Updates an existing stockMarketData.
     *
     * @param id the id of the stockMarketDataDTO to save.
     * @param stockMarketDataDTO the stockMarketDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMarketDataDTO,
     * or with status {@code 400 (Bad Request)} if the stockMarketDataDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockMarketDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/stock-market-data/{id}")
    public ResponseEntity<StockMarketDataDTO> updateStockMarketData(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockMarketDataDTO stockMarketDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to update StockMarketData : {}, {}", id, stockMarketDataDTO);
        if (stockMarketDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMarketDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMarketDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        StockMarketDataDTO result = stockMarketDataService.update(stockMarketDataDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockMarketDataDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /stock-market-data/:id} : Partial updates given fields of an existing stockMarketData, field will ignore if it is null
     *
     * @param id the id of the stockMarketDataDTO to save.
     * @param stockMarketDataDTO the stockMarketDataDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockMarketDataDTO,
     * or with status {@code 400 (Bad Request)} if the stockMarketDataDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockMarketDataDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockMarketDataDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/stock-market-data/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockMarketDataDTO> partialUpdateStockMarketData(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockMarketDataDTO stockMarketDataDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update StockMarketData partially : {}, {}", id, stockMarketDataDTO);
        if (stockMarketDataDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockMarketDataDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockMarketDataRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockMarketDataDTO> result = stockMarketDataService.partialUpdate(stockMarketDataDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockMarketDataDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-market-data} : get all the stockMarketData.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockMarketData in body.
     */
    @GetMapping("/stock-market-data")
    public ResponseEntity<List<StockMarketDataDTO>> getAllStockMarketData(
        StockMarketDataCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get StockMarketData by criteria: {}", criteria);
        Page<StockMarketDataDTO> page = stockMarketDataQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-market-data/count} : count all the stockMarketData.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/stock-market-data/count")
    public ResponseEntity<Long> countStockMarketData(StockMarketDataCriteria criteria) {
        log.debug("REST request to count StockMarketData by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockMarketDataQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-market-data/:id} : get the "id" stockMarketData.
     *
     * @param id the id of the stockMarketDataDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockMarketDataDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/stock-market-data/{id}")
    public ResponseEntity<StockMarketDataDTO> getStockMarketData(@PathVariable Long id) {
        log.debug("REST request to get StockMarketData : {}", id);
        Optional<StockMarketDataDTO> stockMarketDataDTO = stockMarketDataService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockMarketDataDTO);
    }

    /**
     * {@code DELETE  /stock-market-data/:id} : delete the "id" stockMarketData.
     *
     * @param id the id of the stockMarketDataDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/stock-market-data/{id}")
    public ResponseEntity<Void> deleteStockMarketData(@PathVariable Long id) {
        log.debug("REST request to delete StockMarketData : {}", id);
        stockMarketDataService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/stock-market-data?query=:query} : search for the stockMarketData corresponding
     * to the query.
     *
     * @param query the query of the stockMarketData search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/stock-market-data")
    public ResponseEntity<List<StockMarketDataDTO>> searchStockMarketData(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of StockMarketData for query {}", query);
        Page<StockMarketDataDTO> page = stockMarketDataService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
