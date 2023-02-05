package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.Optional;
import org.mgoulene.domain.RealEstateItem;
import org.mgoulene.repository.RealEstateItemRepository;
import org.mgoulene.repository.search.RealEstateItemSearchRepository;
import org.mgoulene.service.dto.RealEstateItemDTO;
import org.mgoulene.service.mapper.RealEstateItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RealEstateItem}.
 */
@Service
@Transactional
public class RealEstateItemService {

    private final Logger log = LoggerFactory.getLogger(RealEstateItemService.class);

    private final RealEstateItemRepository realEstateItemRepository;

    private final RealEstateItemMapper realEstateItemMapper;

    private final RealEstateItemSearchRepository realEstateItemSearchRepository;

    public RealEstateItemService(
        RealEstateItemRepository realEstateItemRepository,
        RealEstateItemMapper realEstateItemMapper,
        RealEstateItemSearchRepository realEstateItemSearchRepository
    ) {
        this.realEstateItemRepository = realEstateItemRepository;
        this.realEstateItemMapper = realEstateItemMapper;
        this.realEstateItemSearchRepository = realEstateItemSearchRepository;
    }

    /**
     * Save a realEstateItem.
     *
     * @param realEstateItemDTO the entity to save.
     * @return the persisted entity.
     */
    public RealEstateItemDTO save(RealEstateItemDTO realEstateItemDTO) {
        log.debug("Request to save RealEstateItem : {}", realEstateItemDTO);
        RealEstateItem realEstateItem = realEstateItemMapper.toEntity(realEstateItemDTO);
        realEstateItem = realEstateItemRepository.save(realEstateItem);
        RealEstateItemDTO result = realEstateItemMapper.toDto(realEstateItem);
        realEstateItemSearchRepository.index(realEstateItem);
        return result;
    }

    /**
     * Update a realEstateItem.
     *
     * @param realEstateItemDTO the entity to save.
     * @return the persisted entity.
     */
    public RealEstateItemDTO update(RealEstateItemDTO realEstateItemDTO) {
        log.debug("Request to update RealEstateItem : {}", realEstateItemDTO);
        RealEstateItem realEstateItem = realEstateItemMapper.toEntity(realEstateItemDTO);
        realEstateItem = realEstateItemRepository.save(realEstateItem);
        RealEstateItemDTO result = realEstateItemMapper.toDto(realEstateItem);
        realEstateItemSearchRepository.index(realEstateItem);
        return result;
    }

    /**
     * Partially update a realEstateItem.
     *
     * @param realEstateItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RealEstateItemDTO> partialUpdate(RealEstateItemDTO realEstateItemDTO) {
        log.debug("Request to partially update RealEstateItem : {}", realEstateItemDTO);

        return realEstateItemRepository
            .findById(realEstateItemDTO.getId())
            .map(existingRealEstateItem -> {
                realEstateItemMapper.partialUpdate(existingRealEstateItem, realEstateItemDTO);

                return existingRealEstateItem;
            })
            .map(realEstateItemRepository::save)
            .map(savedRealEstateItem -> {
                realEstateItemSearchRepository.save(savedRealEstateItem);

                return savedRealEstateItem;
            })
            .map(realEstateItemMapper::toDto);
    }

    /**
     * Get all the realEstateItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RealEstateItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RealEstateItems");
        return realEstateItemRepository.findAll(pageable).map(realEstateItemMapper::toDto);
    }

    /**
     * Get all the realEstateItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<RealEstateItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return realEstateItemRepository.findAllWithEagerRelationships(pageable).map(realEstateItemMapper::toDto);
    }

    /**
     * Get one realEstateItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RealEstateItemDTO> findOne(Long id) {
        log.debug("Request to get RealEstateItem : {}", id);
        return realEstateItemRepository.findOneWithEagerRelationships(id).map(realEstateItemMapper::toDto);
    }

    /**
     * Delete the realEstateItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RealEstateItem : {}", id);
        realEstateItemRepository.deleteById(id);
        realEstateItemSearchRepository.deleteById(id);
    }

    /**
     * Search for the realEstateItem corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RealEstateItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RealEstateItems for query {}", query);
        return realEstateItemSearchRepository.search(query, pageable).map(realEstateItemMapper::toDto);
    }
}
