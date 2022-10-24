package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.Operation;
import org.mgoulene.repository.OperationRepository;
import org.mgoulene.repository.search.OperationSearchRepository;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.mapper.OperationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Operation}.
 */
@Service
@Transactional
public class OperationService {

    private final Logger log = LoggerFactory.getLogger(OperationService.class);

    private final OperationRepository operationRepository;

    private final OperationMapper operationMapper;

    private final OperationSearchRepository operationSearchRepository;

    public OperationService(
        OperationRepository operationRepository,
        OperationMapper operationMapper,
        OperationSearchRepository operationSearchRepository
    ) {
        this.operationRepository = operationRepository;
        this.operationMapper = operationMapper;
        this.operationSearchRepository = operationSearchRepository;
    }

    /**
     * Save a operation.
     *
     * @param operationDTO the entity to save.
     * @return the persisted entity.
     */
    public OperationDTO save(OperationDTO operationDTO) {
        log.debug("Request to save Operation : {}", operationDTO);
        Operation operation = operationMapper.toEntity(operationDTO);
        operation = operationRepository.save(operation);
        OperationDTO result = operationMapper.toDto(operation);
        operationSearchRepository.index(operation);
        return result;
    }

    /**
     * Update a operation.
     *
     * @param operationDTO the entity to save.
     * @return the persisted entity.
     */
    public OperationDTO update(OperationDTO operationDTO) {
        log.debug("Request to update Operation : {}", operationDTO);
        Operation operation = operationMapper.toEntity(operationDTO);
        operation = operationRepository.save(operation);
        OperationDTO result = operationMapper.toDto(operation);
        operationSearchRepository.index(operation);
        return result;
    }

    /**
     * Partially update a operation.
     *
     * @param operationDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<OperationDTO> partialUpdate(OperationDTO operationDTO) {
        log.debug("Request to partially update Operation : {}", operationDTO);

        return operationRepository
            .findById(operationDTO.getId())
            .map(existingOperation -> {
                operationMapper.partialUpdate(existingOperation, operationDTO);

                return existingOperation;
            })
            .map(operationRepository::save)
            .map(savedOperation -> {
                operationSearchRepository.save(savedOperation);

                return savedOperation;
            })
            .map(operationMapper::toDto);
    }

    /**
     * Get all the operations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OperationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Operations");
        return operationRepository.findAll(pageable).map(operationMapper::toDto);
    }

    /**
     * Get all the operations with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<OperationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return operationRepository.findAllWithEagerRelationships(pageable).map(operationMapper::toDto);
    }

    /**
     *  Get all the operations where BudgetItemPeriod is {@code null}.
     *  @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findAllWhereBudgetItemPeriodIsNull() {
        log.debug("Request to get all operations where BudgetItemPeriod is null");
        return StreamSupport
            .stream(operationRepository.findAll().spliterator(), false)
            .filter(operation -> operation.getBudgetItemPeriod() == null)
            .map(operationMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one operation by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<OperationDTO> findOne(Long id) {
        log.debug("Request to get Operation : {}", id);
        return operationRepository.findOneWithEagerRelationships(id).map(operationMapper::toDto);
    }

    /**
     * Delete the operation by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Operation : {}", id);
        operationRepository.deleteById(id);
        operationSearchRepository.deleteById(id);
    }

    /**
     * Search for the operation corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<OperationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Operations for query {}", query);
        return operationSearchRepository.search(query, pageable).map(operationMapper::toDto);
    }
}
