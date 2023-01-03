package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.BankAccount;
import org.mgoulene.repository.BankAccountRepository;
import org.mgoulene.repository.search.BankAccountSearchRepository;
import org.mgoulene.service.criteria.BankAccountCriteria;
import org.mgoulene.service.dto.BankAccountDTO;
import org.mgoulene.service.mapper.BankAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BankAccount} entities in the database.
 * The main input is a {@link BankAccountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BankAccountDTO} or a {@link Page} of {@link BankAccountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BankAccountQueryService extends QueryService<BankAccount> {

    private final Logger log = LoggerFactory.getLogger(BankAccountQueryService.class);

    private final BankAccountRepository bankAccountRepository;

    private final BankAccountMapper bankAccountMapper;

    private final BankAccountSearchRepository bankAccountSearchRepository;

    public BankAccountQueryService(
        BankAccountRepository bankAccountRepository,
        BankAccountMapper bankAccountMapper,
        BankAccountSearchRepository bankAccountSearchRepository
    ) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.bankAccountSearchRepository = bankAccountSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BankAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BankAccountDTO> findByCriteria(BankAccountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BankAccount> specification = createSpecification(criteria);
        return bankAccountMapper.toDto(bankAccountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BankAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BankAccountDTO> findByCriteria(BankAccountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BankAccount> specification = createSpecification(criteria);
        return bankAccountRepository.findAll(specification, page).map(bankAccountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BankAccountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BankAccount> specification = createSpecification(criteria);
        return bankAccountRepository.count(specification);
    }

    /**
     * Function to convert {@link BankAccountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BankAccount> createSpecification(BankAccountCriteria criteria) {
        Specification<BankAccount> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BankAccount_.id));
            }
            if (criteria.getAccountName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountName(), BankAccount_.accountName));
            }
            if (criteria.getAccountBank() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAccountBank(), BankAccount_.accountBank));
            }
            if (criteria.getInitialAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getInitialAmount(), BankAccount_.initialAmount));
            }
            if (criteria.getArchived() != null) {
                specification = specification.and(buildSpecification(criteria.getArchived(), BankAccount_.archived));
            }
            if (criteria.getShortName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortName(), BankAccount_.shortName));
            }
            if (criteria.getAccountType() != null) {
                specification = specification.and(buildSpecification(criteria.getAccountType(), BankAccount_.accountType));
            }
            if (criteria.getAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAccountId(),
                            root -> root.join(BankAccount_.account, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getStockPortfolioItemId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getStockPortfolioItemId(),
                            root -> root.join(BankAccount_.stockPortfolioItems, JoinType.LEFT).get(StockPortfolioItem_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
