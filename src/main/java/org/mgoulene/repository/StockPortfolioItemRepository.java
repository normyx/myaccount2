package org.mgoulene.repository;

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
public interface StockPortfolioItemRepository
    extends JpaRepository<StockPortfolioItem, Long>, JpaSpecificationExecutor<StockPortfolioItem> {
    default Optional<StockPortfolioItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockPortfolioItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockPortfolioItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct stockPortfolioItem from StockPortfolioItem stockPortfolioItem left join fetch stockPortfolioItem.bankAccount",
        countQuery = "select count(distinct stockPortfolioItem) from StockPortfolioItem stockPortfolioItem"
    )
    Page<StockPortfolioItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct stockPortfolioItem from StockPortfolioItem stockPortfolioItem left join fetch stockPortfolioItem.bankAccount")
    List<StockPortfolioItem> findAllWithToOneRelationships();

    @Query(
        "select stockPortfolioItem from StockPortfolioItem stockPortfolioItem left join fetch stockPortfolioItem.bankAccount where stockPortfolioItem.id =:id"
    )
    Optional<StockPortfolioItem> findOneWithToOneRelationships(@Param("id") Long id);
}
