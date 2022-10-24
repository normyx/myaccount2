package org.mgoulene.repository;

import org.mgoulene.domain.BudgetItemPeriod;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BudgetItemPeriod entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BudgetItemPeriodRepository extends JpaRepository<BudgetItemPeriod, Long>, JpaSpecificationExecutor<BudgetItemPeriod> {}
