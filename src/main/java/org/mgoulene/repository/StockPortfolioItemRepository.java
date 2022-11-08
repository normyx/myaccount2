package org.mgoulene.repository;

import org.mgoulene.domain.StockPortfolioItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockPortfolioItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockPortfolioItemRepository
    extends JpaRepository<StockPortfolioItem, Long>, JpaSpecificationExecutor<StockPortfolioItem> {}
