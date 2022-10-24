package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.BudgetItem;
import org.mgoulene.repository.BudgetItemRepository;
import org.mgoulene.repository.search.BudgetItemSearchRepository;
import org.mgoulene.service.criteria.BudgetItemCriteria;
import org.mgoulene.service.dto.BudgetItemDTO;
import org.mgoulene.service.mapper.BudgetItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link BudgetItem} entities in the database.
 * The main input is a {@link BudgetItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link BudgetItemDTO} or a {@link Page} of {@link BudgetItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BudgetItemQueryService extends QueryService<BudgetItem> {

    private final Logger log = LoggerFactory.getLogger(BudgetItemQueryService.class);

    private final BudgetItemRepository budgetItemRepository;

    private final BudgetItemMapper budgetItemMapper;

    private final BudgetItemSearchRepository budgetItemSearchRepository;

    public BudgetItemQueryService(
        BudgetItemRepository budgetItemRepository,
        BudgetItemMapper budgetItemMapper,
        BudgetItemSearchRepository budgetItemSearchRepository
    ) {
        this.budgetItemRepository = budgetItemRepository;
        this.budgetItemMapper = budgetItemMapper;
        this.budgetItemSearchRepository = budgetItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link BudgetItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<BudgetItemDTO> findByCriteria(BudgetItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<BudgetItem> specification = createSpecification(criteria);
        return budgetItemMapper.toDto(budgetItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link BudgetItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BudgetItemDTO> findByCriteria(BudgetItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<BudgetItem> specification = createSpecification(criteria);
        return budgetItemRepository.findAll(specification, page).map(budgetItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BudgetItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<BudgetItem> specification = createSpecification(criteria);
        return budgetItemRepository.count(specification);
    }

    /**
     * Function to convert {@link BudgetItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<BudgetItem> createSpecification(BudgetItemCriteria criteria) {
        Specification<BudgetItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), BudgetItem_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), BudgetItem_.name));
            }
            if (criteria.getOrder() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getOrder(), BudgetItem_.order));
            }
            if (criteria.getBudgetItemPeriodsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBudgetItemPeriodsId(),
                            root -> root.join(BudgetItem_.budgetItemPeriods, JoinType.LEFT).get(BudgetItemPeriod_.id)
                        )
                    );
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCategoryId(),
                            root -> root.join(BudgetItem_.category, JoinType.LEFT).get(Category_.id)
                        )
                    );
            }
            if (criteria.getAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getAccountId(),
                            root -> root.join(BudgetItem_.account, JoinType.LEFT).get(ApplicationUser_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
