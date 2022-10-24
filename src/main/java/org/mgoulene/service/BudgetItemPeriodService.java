package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.BudgetItemPeriod;
import org.mgoulene.repository.BudgetItemPeriodRepository;
import org.mgoulene.repository.search.BudgetItemPeriodSearchRepository;
import org.mgoulene.service.dto.BudgetItemPeriodDTO;
import org.mgoulene.service.mapper.BudgetItemPeriodMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BudgetItemPeriod}.
 */
@Service
@Transactional
public class BudgetItemPeriodService {

    private final Logger log = LoggerFactory.getLogger(BudgetItemPeriodService.class);

    protected final BudgetItemPeriodRepository budgetItemPeriodRepository;

    protected final BudgetItemPeriodMapper budgetItemPeriodMapper;

    private final BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository;

    public BudgetItemPeriodService(
        BudgetItemPeriodRepository budgetItemPeriodRepository,
        BudgetItemPeriodMapper budgetItemPeriodMapper,
        BudgetItemPeriodSearchRepository budgetItemPeriodSearchRepository
    ) {
        this.budgetItemPeriodRepository = budgetItemPeriodRepository;
        this.budgetItemPeriodMapper = budgetItemPeriodMapper;
        this.budgetItemPeriodSearchRepository = budgetItemPeriodSearchRepository;
    }

    /**
     * Save a budgetItemPeriod.
     *
     * @param budgetItemPeriodDTO the entity to save.
     * @return the persisted entity.
     */
    public BudgetItemPeriodDTO save(BudgetItemPeriodDTO budgetItemPeriodDTO) {
        log.debug("Request to save BudgetItemPeriod : {}", budgetItemPeriodDTO);
        BudgetItemPeriod budgetItemPeriod = budgetItemPeriodMapper.toEntity(budgetItemPeriodDTO);
        budgetItemPeriod = budgetItemPeriodRepository.save(budgetItemPeriod);
        BudgetItemPeriodDTO result = budgetItemPeriodMapper.toDto(budgetItemPeriod);
        budgetItemPeriodSearchRepository.index(budgetItemPeriod);
        return result;
    }

    /**
     * Update a budgetItemPeriod.
     *
     * @param budgetItemPeriodDTO the entity to save.
     * @return the persisted entity.
     */
    public BudgetItemPeriodDTO update(BudgetItemPeriodDTO budgetItemPeriodDTO) {
        log.debug("Request to update BudgetItemPeriod : {}", budgetItemPeriodDTO);
        BudgetItemPeriod budgetItemPeriod = budgetItemPeriodMapper.toEntity(budgetItemPeriodDTO);
        budgetItemPeriod = budgetItemPeriodRepository.save(budgetItemPeriod);
        BudgetItemPeriodDTO result = budgetItemPeriodMapper.toDto(budgetItemPeriod);
        budgetItemPeriodSearchRepository.index(budgetItemPeriod);
        return result;
    }

    /**
     * Partially update a budgetItemPeriod.
     *
     * @param budgetItemPeriodDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BudgetItemPeriodDTO> partialUpdate(BudgetItemPeriodDTO budgetItemPeriodDTO) {
        log.debug("Request to partially update BudgetItemPeriod : {}", budgetItemPeriodDTO);

        return budgetItemPeriodRepository
            .findById(budgetItemPeriodDTO.getId())
            .map(existingBudgetItemPeriod -> {
                budgetItemPeriodMapper.partialUpdate(existingBudgetItemPeriod, budgetItemPeriodDTO);

                return existingBudgetItemPeriod;
            })
            .map(budgetItemPeriodRepository::save)
            .map(savedBudgetItemPeriod -> {
                budgetItemPeriodSearchRepository.save(savedBudgetItemPeriod);

                return savedBudgetItemPeriod;
            })
            .map(budgetItemPeriodMapper::toDto);
    }

    /**
     * Get all the budgetItemPeriods.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BudgetItemPeriodDTO> findAll() {
        log.debug("Request to get all BudgetItemPeriods");
        return budgetItemPeriodRepository
            .findAll()
            .stream()
            .map(budgetItemPeriodMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one budgetItemPeriod by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BudgetItemPeriodDTO> findOne(Long id) {
        log.debug("Request to get BudgetItemPeriod : {}", id);
        return budgetItemPeriodRepository.findById(id).map(budgetItemPeriodMapper::toDto);
    }

    /**
     * Delete the budgetItemPeriod by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BudgetItemPeriod : {}", id);
        budgetItemPeriodRepository.deleteById(id);
        budgetItemPeriodSearchRepository.deleteById(id);
    }

    /**
     * Search for the budgetItemPeriod corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BudgetItemPeriodDTO> search(String query) {
        log.debug("Request to search BudgetItemPeriods for query {}", query);
        return StreamSupport
            .stream(budgetItemPeriodSearchRepository.search(query).spliterator(), false)
            .map(budgetItemPeriodMapper::toDto)
            .collect(Collectors.toList());
    }
}
