package org.mgoulene.mya.repository;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.StockPortfolioItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockPortfolioItem entity.
 */
@Repository
public interface MyaStockPortfolioItemRepository
    extends JpaRepository<StockPortfolioItem, Long>, JpaSpecificationExecutor<StockPortfolioItem> {
    @Query(
        "select distinct stockPortfolioItem from StockPortfolioItem stockPortfolioItem where stockPortfolioItem.stockSymbol = :symbol and stockPortfolioItem.bankAccount.account.id = :applicationId"
    )
    List<StockPortfolioItem> findAllWithSymbolAndApplicationUser(
        @Param("symbol") String symbol,
        @Param("applicationId") Long applicationId
    );

    @Query(
        "select distinct stockPortfolioItem from StockPortfolioItem stockPortfolioItem  where stockPortfolioItem.bankAccount.account.id = :applicationId"
    )
    List<StockPortfolioItem> findAllWithApplicationUser(@Param("applicationId") Long applicationId);
}
