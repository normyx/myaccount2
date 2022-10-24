package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.SubCategory;
import org.mgoulene.repository.SubCategoryRepository;
import org.mgoulene.repository.search.SubCategorySearchRepository;
import org.mgoulene.service.criteria.SubCategoryCriteria;
import org.mgoulene.service.dto.SubCategoryDTO;
import org.mgoulene.service.mapper.SubCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link SubCategory} entities in the database.
 * The main input is a {@link SubCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SubCategoryDTO} or a {@link Page} of {@link SubCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SubCategoryQueryService extends QueryService<SubCategory> {

    private final Logger log = LoggerFactory.getLogger(SubCategoryQueryService.class);

    private final SubCategoryRepository subCategoryRepository;

    private final SubCategoryMapper subCategoryMapper;

    private final SubCategorySearchRepository subCategorySearchRepository;

    public SubCategoryQueryService(
        SubCategoryRepository subCategoryRepository,
        SubCategoryMapper subCategoryMapper,
        SubCategorySearchRepository subCategorySearchRepository
    ) {
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryMapper = subCategoryMapper;
        this.subCategorySearchRepository = subCategorySearchRepository;
    }

    /**
     * Return a {@link List} of {@link SubCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SubCategoryDTO> findByCriteria(SubCategoryCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<SubCategory> specification = createSpecification(criteria);
        return subCategoryMapper.toDto(subCategoryRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SubCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SubCategoryDTO> findByCriteria(SubCategoryCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<SubCategory> specification = createSpecification(criteria);
        return subCategoryRepository.findAll(specification, page).map(subCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SubCategoryCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<SubCategory> specification = createSpecification(criteria);
        return subCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link SubCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<SubCategory> createSpecification(SubCategoryCriteria criteria) {
        Specification<SubCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), SubCategory_.id));
            }
            if (criteria.getSubCategoryName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSubCategoryName(), SubCategory_.subCategoryName));
            }
            if (criteria.getCategoryId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getCategoryId(),
                            root -> root.join(SubCategory_.category, JoinType.LEFT).get(Category_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
