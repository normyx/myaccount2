package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.RealEstateItem;
import org.mgoulene.repository.RealEstateItemRepository;
import org.mgoulene.repository.search.RealEstateItemSearchRepository;
import org.mgoulene.service.criteria.RealEstateItemCriteria;
import org.mgoulene.service.dto.RealEstateItemDTO;
import org.mgoulene.service.mapper.RealEstateItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link RealEstateItem} entities in the database.
 * The main input is a {@link RealEstateItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RealEstateItemDTO} or a {@link Page} of {@link RealEstateItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RealEstateItemQueryService extends QueryService<RealEstateItem> {

    private final Logger log = LoggerFactory.getLogger(RealEstateItemQueryService.class);

    private final RealEstateItemRepository realEstateItemRepository;

    private final RealEstateItemMapper realEstateItemMapper;

    private final RealEstateItemSearchRepository realEstateItemSearchRepository;

    public RealEstateItemQueryService(
        RealEstateItemRepository realEstateItemRepository,
        RealEstateItemMapper realEstateItemMapper,
        RealEstateItemSearchRepository realEstateItemSearchRepository
    ) {
        this.realEstateItemRepository = realEstateItemRepository;
        this.realEstateItemMapper = realEstateItemMapper;
        this.realEstateItemSearchRepository = realEstateItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RealEstateItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RealEstateItemDTO> findByCriteria(RealEstateItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RealEstateItem> specification = createSpecification(criteria);
        return realEstateItemMapper.toDto(realEstateItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RealEstateItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RealEstateItemDTO> findByCriteria(RealEstateItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RealEstateItem> specification = createSpecification(criteria);
        return realEstateItemRepository.findAll(specification, page).map(realEstateItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RealEstateItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RealEstateItem> specification = createSpecification(criteria);
        return realEstateItemRepository.count(specification);
    }

    /**
     * Function to convert {@link RealEstateItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RealEstateItem> createSpecification(RealEstateItemCriteria criteria) {
        Specification<RealEstateItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RealEstateItem_.id));
            }
            if (criteria.getLoanValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLoanValue(), RealEstateItem_.loanValue));
            }
            if (criteria.getTotalValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalValue(), RealEstateItem_.totalValue));
            }
            if (criteria.getPercentOwned() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPercentOwned(), RealEstateItem_.percentOwned));
            }
            if (criteria.getItemDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getItemDate(), RealEstateItem_.itemDate));
            }
            if (criteria.getBankAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBankAccountId(),
                            root -> root.join(RealEstateItem_.bankAccount, JoinType.LEFT).get(BankAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
