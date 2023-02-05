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
import org.mgoulene.repository.RealEstateItemRepository;
import org.mgoulene.service.RealEstateItemQueryService;
import org.mgoulene.service.RealEstateItemService;
import org.mgoulene.service.criteria.RealEstateItemCriteria;
import org.mgoulene.service.dto.RealEstateItemDTO;
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
 * REST controller for managing {@link org.mgoulene.domain.RealEstateItem}.
 */
@RestController
@RequestMapping("/api")
public class RealEstateItemResource {

    private final Logger log = LoggerFactory.getLogger(RealEstateItemResource.class);

    private static final String ENTITY_NAME = "realEstateItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RealEstateItemService realEstateItemService;

    private final RealEstateItemRepository realEstateItemRepository;

    private final RealEstateItemQueryService realEstateItemQueryService;

    public RealEstateItemResource(
        RealEstateItemService realEstateItemService,
        RealEstateItemRepository realEstateItemRepository,
        RealEstateItemQueryService realEstateItemQueryService
    ) {
        this.realEstateItemService = realEstateItemService;
        this.realEstateItemRepository = realEstateItemRepository;
        this.realEstateItemQueryService = realEstateItemQueryService;
    }

    /**
     * {@code POST  /real-estate-items} : Create a new realEstateItem.
     *
     * @param realEstateItemDTO the realEstateItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new realEstateItemDTO, or with status {@code 400 (Bad Request)} if the realEstateItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/real-estate-items")
    public ResponseEntity<RealEstateItemDTO> createRealEstateItem(@Valid @RequestBody RealEstateItemDTO realEstateItemDTO)
        throws URISyntaxException {
        log.debug("REST request to save RealEstateItem : {}", realEstateItemDTO);
        if (realEstateItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new realEstateItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RealEstateItemDTO result = realEstateItemService.save(realEstateItemDTO);
        return ResponseEntity
            .created(new URI("/api/real-estate-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /real-estate-items/:id} : Updates an existing realEstateItem.
     *
     * @param id the id of the realEstateItemDTO to save.
     * @param realEstateItemDTO the realEstateItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated realEstateItemDTO,
     * or with status {@code 400 (Bad Request)} if the realEstateItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the realEstateItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/real-estate-items/{id}")
    public ResponseEntity<RealEstateItemDTO> updateRealEstateItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RealEstateItemDTO realEstateItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RealEstateItem : {}, {}", id, realEstateItemDTO);
        if (realEstateItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, realEstateItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!realEstateItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RealEstateItemDTO result = realEstateItemService.update(realEstateItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, realEstateItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /real-estate-items/:id} : Partial updates given fields of an existing realEstateItem, field will ignore if it is null
     *
     * @param id the id of the realEstateItemDTO to save.
     * @param realEstateItemDTO the realEstateItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated realEstateItemDTO,
     * or with status {@code 400 (Bad Request)} if the realEstateItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the realEstateItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the realEstateItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/real-estate-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RealEstateItemDTO> partialUpdateRealEstateItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RealEstateItemDTO realEstateItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RealEstateItem partially : {}, {}", id, realEstateItemDTO);
        if (realEstateItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, realEstateItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!realEstateItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RealEstateItemDTO> result = realEstateItemService.partialUpdate(realEstateItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, realEstateItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /real-estate-items} : get all the realEstateItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of realEstateItems in body.
     */
    @GetMapping("/real-estate-items")
    public ResponseEntity<List<RealEstateItemDTO>> getAllRealEstateItems(
        RealEstateItemCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get RealEstateItems by criteria: {}", criteria);
        Page<RealEstateItemDTO> page = realEstateItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /real-estate-items/count} : count all the realEstateItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/real-estate-items/count")
    public ResponseEntity<Long> countRealEstateItems(RealEstateItemCriteria criteria) {
        log.debug("REST request to count RealEstateItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(realEstateItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /real-estate-items/:id} : get the "id" realEstateItem.
     *
     * @param id the id of the realEstateItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the realEstateItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/real-estate-items/{id}")
    public ResponseEntity<RealEstateItemDTO> getRealEstateItem(@PathVariable Long id) {
        log.debug("REST request to get RealEstateItem : {}", id);
        Optional<RealEstateItemDTO> realEstateItemDTO = realEstateItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(realEstateItemDTO);
    }

    /**
     * {@code DELETE  /real-estate-items/:id} : delete the "id" realEstateItem.
     *
     * @param id the id of the realEstateItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/real-estate-items/{id}")
    public ResponseEntity<Void> deleteRealEstateItem(@PathVariable Long id) {
        log.debug("REST request to delete RealEstateItem : {}", id);
        realEstateItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/real-estate-items?query=:query} : search for the realEstateItem corresponding
     * to the query.
     *
     * @param query the query of the realEstateItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/real-estate-items")
    public ResponseEntity<List<RealEstateItemDTO>> searchRealEstateItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of RealEstateItems for query {}", query);
        Page<RealEstateItemDTO> page = realEstateItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
