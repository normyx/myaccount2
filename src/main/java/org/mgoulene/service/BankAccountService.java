package org.mgoulene.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.mgoulene.domain.BankAccount;
import org.mgoulene.repository.BankAccountRepository;
import org.mgoulene.repository.search.BankAccountSearchRepository;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.mapper.BankAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BankAccount}.
 */
@Service
@Transactional
public class BankAccountService {

    private final Logger log = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository bankAccountRepository;

    protected final BankAccountMapper bankAccountMapper;

    private final BankAccountSearchRepository bankAccountSearchRepository;

    public BankAccountService(
        BankAccountRepository bankAccountRepository,
        BankAccountMapper bankAccountMapper,
        BankAccountSearchRepository bankAccountSearchRepository
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.bankAccountSearchRepository = bankAccountSearchRepository;
    }

    /**
     * Save a bankAccount.
     *
     * @param bankAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public BankAccountDTO save(BankAccountDTO bankAccountDTO) {
        log.debug("Request to save BankAccount : {}", bankAccountDTO);
        BankAccount bankAccount = bankAccountMapper.toEntity(bankAccountDTO);
        bankAccount = bankAccountRepository.save(bankAccount);
        BankAccountDTO result = bankAccountMapper.toDto(bankAccount);
        bankAccountSearchRepository.index(bankAccount);
        return result;
    }

    /**
     * Update a bankAccount.
     *
     * @param bankAccountDTO the entity to save.
     * @return the persisted entity.
     */
    public BankAccountDTO update(BankAccountDTO bankAccountDTO) {
        log.debug("Request to update BankAccount : {}", bankAccountDTO);
        BankAccount bankAccount = bankAccountMapper.toEntity(bankAccountDTO);
        bankAccount = bankAccountRepository.save(bankAccount);
        BankAccountDTO result = bankAccountMapper.toDto(bankAccount);
        bankAccountSearchRepository.index(bankAccount);
        return result;
    }

    /**
     * Partially update a bankAccount.
     *
     * @param bankAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BankAccountDTO> partialUpdate(BankAccountDTO bankAccountDTO) {
        log.debug("Request to partially update BankAccount : {}", bankAccountDTO);

        return bankAccountRepository
            .findById(bankAccountDTO.getId())
            .map(existingBankAccount -> {
                bankAccountMapper.partialUpdate(existingBankAccount, bankAccountDTO);

                return existingBankAccount;
            })
            .map(bankAccountRepository::save)
            .map(savedBankAccount -> {
                bankAccountSearchRepository.save(savedBankAccount);

                return savedBankAccount;
            })
            .map(bankAccountMapper::toDto);
    }

    /**
     * Get all the bankAccounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BankAccountDTO> findAll() {
        log.debug("Request to get all BankAccounts");
        return bankAccountRepository.findAll().stream().map(bankAccountMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the bankAccounts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<BankAccountDTO> findAllWithEagerRelationships(Pageable pageable) {
        return bankAccountRepository.findAllWithEagerRelationships(pageable).map(bankAccountMapper::toDto);
    }

    /**
     * Get one bankAccount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BankAccountDTO> findOne(Long id) {
        log.debug("Request to get BankAccount : {}", id);
        return bankAccountRepository.findOneWithEagerRelationships(id).map(bankAccountMapper::toDto);
    }

    /**
     * Delete the bankAccount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete BankAccount : {}", id);
        bankAccountRepository.deleteById(id);
        bankAccountSearchRepository.deleteById(id);
    }

    /**
     * Search for the bankAccount corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BankAccountDTO> search(String query) {
        log.debug("Request to search BankAccounts for query {}", query);
        return StreamSupport
            .stream(bankAccountSearchRepository.search(query).spliterator(), false)
            .map(bankAccountMapper::toDto)
            .collect(Collectors.toList());
    }
}
