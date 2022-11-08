package org.mgoulene.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.mgoulene.domain.*; // for static metamodels
import org.mgoulene.domain.StockPortfolioItem;
import org.mgoulene.repository.StockPortfolioItemRepository;
import org.mgoulene.repository.search.StockPortfolioItemSearchRepository;
import org.mgoulene.service.criteria.StockPortfolioItemCriteria;
import org.mgoulene.service.dto.StockPortfolioItemDTO;
import org.mgoulene.service.mapper.StockPortfolioItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StockPortfolioItem} entities in the database.
 * The main input is a {@link StockPortfolioItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link StockPortfolioItemDTO} or a {@link Page} of {@link StockPortfolioItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockPortfolioItemQueryService extends QueryService<StockPortfolioItem> {

    private final Logger log = LoggerFactory.getLogger(StockPortfolioItemQueryService.class);

    private final StockPortfolioItemRepository stockPortfolioItemRepository;

    private final StockPortfolioItemMapper stockPortfolioItemMapper;

    private final StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository;

    public StockPortfolioItemQueryService(
        StockPortfolioItemRepository stockPortfolioItemRepository,
        StockPortfolioItemMapper stockPortfolioItemMapper,
        StockPortfolioItemSearchRepository stockPortfolioItemSearchRepository
    ) {
        this.stockPortfolioItemRepository = stockPortfolioItemRepository;
        this.stockPortfolioItemMapper = stockPortfolioItemMapper;
        this.stockPortfolioItemSearchRepository = stockPortfolioItemSearchRepository;
    }

    /**
     * Return a {@link List} of {@link StockPortfolioItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<StockPortfolioItemDTO> findByCriteria(StockPortfolioItemCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<StockPortfolioItem> specification = createSpecification(criteria);
        return stockPortfolioItemMapper.toDto(stockPortfolioItemRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link StockPortfolioItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockPortfolioItemDTO> findByCriteria(StockPortfolioItemCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockPortfolioItem> specification = createSpecification(criteria);
        return stockPortfolioItemRepository.findAll(specification, page).map(stockPortfolioItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockPortfolioItemCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<StockPortfolioItem> specification = createSpecification(criteria);
        return stockPortfolioItemRepository.count(specification);
    }

    /**
     * Function to convert {@link StockPortfolioItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockPortfolioItem> createSpecification(StockPortfolioItemCriteria criteria) {
        Specification<StockPortfolioItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockPortfolioItem_.id));
            }
            if (criteria.getStockSymbol() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStockSymbol(), StockPortfolioItem_.stockSymbol));
            }
            if (criteria.getStockCurrency() != null) {
                specification = specification.and(buildSpecification(criteria.getStockCurrency(), StockPortfolioItem_.stockCurrency));
            }
            if (criteria.getStockAcquisitionDate() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getStockAcquisitionDate(), StockPortfolioItem_.stockAcquisitionDate)
                    );
            }
            if (criteria.getStockSharesNumber() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getStockSharesNumber(), StockPortfolioItem_.stockSharesNumber));
            }
            if (criteria.getStockAcquisitionPrice() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getStockAcquisitionPrice(), StockPortfolioItem_.stockAcquisitionPrice)
                    );
            }
            if (criteria.getStockCurrentPrice() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getStockCurrentPrice(), StockPortfolioItem_.stockCurrentPrice));
            }
            if (criteria.getStockCurrentDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getStockCurrentDate(), StockPortfolioItem_.stockCurrentDate));
            }
            if (criteria.getStockAcquisitionCurrencyFactor() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(
                            criteria.getStockAcquisitionCurrencyFactor(),
                            StockPortfolioItem_.stockAcquisitionCurrencyFactor
                        )
                    );
            }
            if (criteria.getStockCurrentCurrencyFactor() != null) {
                specification =
                    specification.and(
                        buildRangeSpecification(criteria.getStockCurrentCurrencyFactor(), StockPortfolioItem_.stockCurrentCurrencyFactor)
                    );
            }
            if (criteria.getBankAccountId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getBankAccountId(),
                            root -> root.join(StockPortfolioItem_.bankAccounts, JoinType.LEFT).get(BankAccount_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
