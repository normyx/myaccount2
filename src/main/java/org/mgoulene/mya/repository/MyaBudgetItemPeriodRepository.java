package org.mgoulene.mya.repository;

import java.time.LocalDate;
import org.mgoulene.domain.BudgetItemPeriod;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BudgetItemPeriod entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MyaBudgetItemPeriodRepository extends JpaRepository<BudgetItemPeriod, Long>, JpaSpecificationExecutor<BudgetItemPeriod> {
    @Query(
        "SELECT bip FROM BudgetItemPeriod bip WHERE bip.budgetItem.id =:budgetItemId AND bip.month = (SELECT MAX(bip2.month) FROM BudgetItemPeriod bip2 WHERE bip2.budgetItem.id =:budgetItemId)"
    )
    BudgetItemPeriod findLastBudgetItemPeriod(@Param("budgetItemId") Long budgetItemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE BudgetItemPeriod bip WHERE bip.budgetItem.id =:budgetItemId AND bip.month >=:month ")
    void deleteWithNext(@Param("budgetItemId") Long budgetItemId, @Param("month") LocalDate month);

    @Modifying(clearAutomatically = true)
    @Query("DELETE BudgetItemPeriod bip WHERE bip.budgetItem.id =:budgetItemId")
    void deleteFromBudgetItem(@Param("budgetItemId") Long budgetItemId);
}
