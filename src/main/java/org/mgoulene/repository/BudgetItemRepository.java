package org.mgoulene.repository;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.BudgetItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BudgetItem entity.
 */
@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long>, JpaSpecificationExecutor<BudgetItem> {
    default Optional<BudgetItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BudgetItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BudgetItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct budgetItem from BudgetItem budgetItem left join fetch budgetItem.category left join fetch budgetItem.account",
        countQuery = "select count(distinct budgetItem) from BudgetItem budgetItem"
    )
    Page<BudgetItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct budgetItem from BudgetItem budgetItem left join fetch budgetItem.category left join fetch budgetItem.account")
    List<BudgetItem> findAllWithToOneRelationships();

    @Query(
        "select budgetItem from BudgetItem budgetItem left join fetch budgetItem.category left join fetch budgetItem.account where budgetItem.id =:id"
    )
    Optional<BudgetItem> findOneWithToOneRelationships(@Param("id") Long id);
}
