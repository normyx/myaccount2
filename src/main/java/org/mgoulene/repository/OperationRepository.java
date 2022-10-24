package org.mgoulene.repository;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Operation entity.
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long>, JpaSpecificationExecutor<Operation> {
    default Optional<Operation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Operation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Operation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct operation from Operation operation left join fetch operation.subCategory left join fetch operation.account",
        countQuery = "select count(distinct operation) from Operation operation"
    )
    Page<Operation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct operation from Operation operation left join fetch operation.subCategory left join fetch operation.account")
    List<Operation> findAllWithToOneRelationships();

    @Query(
        "select operation from Operation operation left join fetch operation.subCategory left join fetch operation.account where operation.id =:id"
    )
    Optional<Operation> findOneWithToOneRelationships(@Param("id") Long id);
}
