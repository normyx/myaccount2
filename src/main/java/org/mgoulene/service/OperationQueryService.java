package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.Operation;
import org.mgoulene.repository.OperationRepository;
import org.mgoulene.repository.search.OperationSearchRepository;
import org.mgoulene.service.criteria.OperationCriteria;
import org.mgoulene.service.dto.OperationDTO;
import org.mgoulene.service.mapper.OperationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Operation} entities in the database.
 * The main input is a {@link OperationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link OperationDTO} or a {@link Page} of {@link OperationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OperationQueryService extends QueryService<Operation> {

    private final Logger log = LoggerFactory.getLogger(OperationQueryService.class);

    private final OperationRepository operationRepository;

    private final OperationMapper operationMapper;

    private final OperationSearchRepository operationSearchRepository;

    public OperationQueryService(
        OperationRepository operationRepository,
        OperationMapper operationMapper,
        OperationSearchRepository operationSearchRepository
    ) {
        this.operationRepository = operationRepository;
        this.operationMapper = operationMapper;
        this.operationSearchRepository = operationSearchRepository;
    }

    /**
     * Return a {@link List} of {@link OperationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<OperationDTO> findByCriteria(OperationCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Operation> specification = createSpecification(criteria);
        return operationMapper.toDto(operationRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link OperationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OperationDTO> findByCriteria(OperationCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Operation> specification = createSpecification(criteria);
        return operationRepository.findAll(specification, page).map(operationMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OperationCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Operation> specification = createSpecification(criteria);
        return operationRepository.count(specification);
    }

    /**
     * Function to convert {@link OperationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Operation> createSpecification(OperationCriteria criteria) {
        Specification<Operation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Operation_.id));
            }
            if (criteria.getLabel() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLabel(), Operation_.label));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Operation_.date));
            }
            if (criteria.getAmount() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAmount(), Operation_.amount));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), Operation_.note));
            }
            if (criteria.getCheckNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCheckNumber(), Operation_.checkNumber));
            }
            if (criteria.getIsUpToDate() != null) {
                specification = specification.and(buildSpecification(criteria.getIsUpToDate(), Operation_.isUpToDate));
            }
            if (criteria.getDeletingHardLock() != null) {
                specification = specification.and(buildSpecification(criteria.getDeletingHardLock(), Operation_.deletingHardLock));
            }
            if (criteria.getSubCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getSubCategoryId(),
                            root -> root.join(Operation_.subCategory, JoinType.LEFT).get(SubCategory_.id)
                        )
                    );
            }
            if (criteria.getAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAccountId(),
                            root -> root.join(Operation_.account, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
            if (criteria.getBankAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBankAccountId(),
                            root -> root.join(Operation_.bankAccount, JoinType.LEFT).get(BankAccount_.id)
                        )
                    );
            }
            if (criteria.getBudgetItemPeriodId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBudgetItemPeriodId(),
                            root -> root.join(Operation_.budgetItemPeriod, JoinType.LEFT).get(BudgetItemPeriod_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
