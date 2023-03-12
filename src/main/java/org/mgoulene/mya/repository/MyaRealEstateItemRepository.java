package org.mgoulene.mya.repository;

import java.util.List;
import org.mgoulene.domain.RealEstateItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RealEstateItem entity.
 */
@Repository
public interface MyaRealEstateItemRepository extends JpaRepository<RealEstateItem, Long>, JpaSpecificationExecutor<RealEstateItem> {
    @Query(
        "select realEstateItem from RealEstateItem realEstateItem where realEstateItem.bankAccount.id =:bankAccountId order by realEstateItem.itemDate desc"
    )
    List<RealEstateItem> findAllFromBankAccount(@Param("bankAccountId") Long id);
}
