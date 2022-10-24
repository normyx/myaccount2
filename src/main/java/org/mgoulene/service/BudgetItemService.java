package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.repository.search.BudgetItemSearchRepository;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BudgetItem}.
 */
@Service
@Transactional
public class BudgetItemService {

    private final Logger log = LoggerFactory.getLogger(BudgetItemService.class);

    protected final BudgetItemRepository budgetItemRepository;

    protected final BudgetItemMapper budgetItemMapper;

    private final BudgetItemSearchRepository budgetItemSearchRepository;

    public BudgetItemService(
        BudgetItemRepository budgetItemRepository,
        BudgetItemMapper budgetItemMapper,
        BudgetItemSearchRepository budgetItemSearchRepository
    ) {
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemMapper = budgetItemMapper;
        this.budgetItemSearchRepository = budgetItemSearchRepository;
    }

    /**
     * Save a budgetItem.
     *
     * @param budgetItemDTO the entity to save.
     * @return the persisted entity.
     */
    public BudgetItemDTO save(BudgetItemDTO budgetItemDTO) {
        log.debug("Request to save BudgetItem : {}", budgetItemDTO);
        BudgetItem budgetItem = budgetItemMapper.toEntity(budgetItemDTO);
        budgetItem = budgetItemRepository.save(budgetItem);
        BudgetItemDTO result = budgetItemMapper.toDto(budgetItem);
        budgetItemSearchRepository.index(budgetItem);
        return result;
    }

    /**
     * Update a budgetItem.
     *
     * @param budgetItemDTO the entity to save.
     * @return the persisted entity.
     */
    public BudgetItemDTO update(BudgetItemDTO budgetItemDTO) {
        log.debug("Request to update BudgetItem : {}", budgetItemDTO);
        BudgetItem budgetItem = budgetItemMapper.toEntity(budgetItemDTO);
        budgetItem = budgetItemRepository.save(budgetItem);
        BudgetItemDTO result = budgetItemMapper.toDto(budgetItem);
        budgetItemSearchRepository.index(budgetItem);
        return result;
    }

    /**
     * Partially update a budgetItem.
     *
     * @param budgetItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BudgetItemDTO> partialUpdate(BudgetItemDTO budgetItemDTO) {
        log.debug("Request to partially update BudgetItem : {}", budgetItemDTO);

        return budgetItemRepository
            .findById(budgetItemDTO.getId())
            .map(existingBudgetItem -> {
                budgetItemMapper.partialUpdate(existingBudgetItem, budgetItemDTO);

                return existingBudgetItem;
            })
            .map(budgetItemRepository::save)
            .map(savedBudgetItem -> {
                budgetItemSearchRepository.save(savedBudgetItem);

                return savedBudgetItem;
            })
            .map(budgetItemMapper::toDto);
    }

    /**
     * Get all the budgetItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BudgetItemDTO> findAll() {
        log.debug("Request to get all BudgetItems");
        return budgetItemRepository.findAll().stream().map(budgetItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the budgetItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BudgetItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return budgetItemRepository.findAllWithEagerRelationships(pageable).map(budgetItemMapper::toDto);
    }

    /**
     * Get one budgetItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BudgetItemDTO> findOne(Long id) {
        log.debug("Request to get BudgetItem : {}", id);
        return budgetItemRepository.findOneWithEagerRelationships(id).map(budgetItemMapper::toDto);
    }

    /**
     * Delete the budgetItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BudgetItem : {}", id);
        budgetItemRepository.deleteById(id);
        budgetItemSearchRepository.deleteById(id);
    }

    /**
     * Search for the budgetItem corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BudgetItemDTO> search(String query) {
        log.debug("Request to search BudgetItems for query {}", query);
        return StreamSupport
            .stream(budgetItemSearchRepository.search(query).spliterator(), false)
            .map(budgetItemMapper::toDto)
            .collect(Collectors.toList());
    }
}
