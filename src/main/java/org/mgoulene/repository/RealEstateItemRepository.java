package org.mgoulene.repository;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.RealEstateItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RealEstateItem entity.
 */
@Repository
public interface RealEstateItemRepository extends JpaRepository<RealEstateItem, Long>, JpaSpecificationExecutor<RealEstateItem> {
    default Optional<RealEstateItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RealEstateItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RealEstateItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct realEstateItem from RealEstateItem realEstateItem left join fetch realEstateItem.bankAccount",
        countQuery = "select count(distinct realEstateItem) from RealEstateItem realEstateItem"
    )
    Page<RealEstateItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct realEstateItem from RealEstateItem realEstateItem left join fetch realEstateItem.bankAccount")
    List<RealEstateItem> findAllWithToOneRelationships();

    @Query(
        "select realEstateItem from RealEstateItem realEstateItem left join fetch realEstateItem.bankAccount where realEstateItem.id =:id"
    )
    Optional<RealEstateItem> findOneWithToOneRelationships(@Param("id") Long id);
}
