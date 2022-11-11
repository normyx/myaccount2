package org.mgoulene.repository;

import org.mgoulene.domain.StockMarketData;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockMarketData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockMarketDataRepository extends JpaRepository<StockMarketData, Long>, JpaSpecificationExecutor<StockMarketData> {}
