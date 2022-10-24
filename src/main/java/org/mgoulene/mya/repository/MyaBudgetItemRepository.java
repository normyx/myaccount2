package org.mgoulene.mya.repository;

import java.time.LocalDate;
import java.util.List;
import org.mgoulene.domain.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BudgetItem entity.
 */
@Repository
public interface MyaBudgetItemRepository extends JpaRepository<BudgetItem, Long>, JpaSpecificationExecutor<BudgetItem> {
    @Query(
        "select distinct budgetItem from BudgetItem budgetItem where budgetItem.account.id =:accountId AND (select count(bip) from BudgetItemPeriod bip where bip.budgetItem = budgetItem AND bip.month >= :from AND bip.month < :to) != 0"
    )
    List<BudgetItem> findAllAvailableBetweenDate(
        @Param("accountId") Long accountId,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    @Query(
        "select distinct budgetItem from BudgetItem budgetItem where budgetItem.account.id =:accountId AND budgetItem.category.id =:categoryId AND (select count(bip) from BudgetItemPeriod bip where bip.budgetItem = budgetItem AND bip.month >= :from AND bip.month < :to) != 0"
    )
    List<BudgetItem> findAllAvailableBetweenDateWithCategory(
        @Param("accountId") Long accountId,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to,
        @Param("categoryId") Long categoryId
    );

    @Modifying
    @Query("UPDATE BudgetItem bi SET bi.order = bi.order + 1 WHERE bi.order >= :order and bi.account.id = :accountId")
    int increaseOrder(@Param("order") Integer order, @Param("accountId") Long accountId);

    @Modifying
    @Query("UPDATE BudgetItem bi SET bi.order = bi.order - 1 WHERE bi.order <= :order and bi.account.id = :accountId")
    int decreaseOrder(@Param("order") Integer order, @Param("accountId") Long accountId);

    @Query("SELECT MAX(bi.order) + 1 FROM BudgetItem as bi WHERE bi.account.id = :accountId")
    Integer findNewOrder(@Param("accountId") Long accountId);
}
