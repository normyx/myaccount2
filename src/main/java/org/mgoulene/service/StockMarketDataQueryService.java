package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.StockMarketData;
import org.mgoulene.repository.StockMarketDataRepository;
import org.mgoulene.repository.search.StockMarketDataSearchRepository;
import org.mgoulene.service.criteria.StockMarketDataCriteria;
import org.mgoulene.service.dto.StockMarketDataDTO;
import org.mgoulene.service.mapper.StockMarketDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StockMarketData} entities in the database.
 * The main input is a {@link StockMarketDataCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockMarketDataDTO} or a {@link Page} of {@link StockMarketDataDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockMarketDataQueryService extends QueryService<StockMarketData> {

    private final Logger log = LoggerFactory.getLogger(StockMarketDataQueryService.class);

    private final StockMarketDataRepository stockMarketDataRepository;

    private final StockMarketDataMapper stockMarketDataMapper;

    private final StockMarketDataSearchRepository stockMarketDataSearchRepository;

    public StockMarketDataQueryService(
        StockMarketDataRepository stockMarketDataRepository,
        StockMarketDataMapper stockMarketDataMapper,
        StockMarketDataSearchRepository stockMarketDataSearchRepository
    ) {
        this.stockMarketDataRepository = stockMarketDataRepository;
        this.stockMarketDataMapper = stockMarketDataMapper;
        this.stockMarketDataSearchRepository = stockMarketDataSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StockMarketDataDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockMarketDataDTO> findByCriteria(StockMarketDataCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockMarketData> specification = createSpecification(criteria);
        return stockMarketDataMapper.toDto(stockMarketDataRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockMarketDataDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockMarketDataDTO> findByCriteria(StockMarketDataCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockMarketData> specification = createSpecification(criteria);
        return stockMarketDataRepository.findAll(specification, page).map(stockMarketDataMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockMarketDataCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockMarketData> specification = createSpecification(criteria);
        return stockMarketDataRepository.count(specification);
    }

    /**
     * Function to convert {@link StockMarketDataCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockMarketData> createSpecification(StockMarketDataCriteria criteria) {
        Specification<StockMarketData> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockMarketData_.id));
            }
            if (criteria.getSymbol() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSymbol(), StockMarketData_.symbol));
            }
            if (criteria.getDataDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDataDate(), StockMarketData_.dataDate));
            }
            if (criteria.getCloseValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCloseValue(), StockMarketData_.closeValue));
            }
        }
        return specification;
    }
}
