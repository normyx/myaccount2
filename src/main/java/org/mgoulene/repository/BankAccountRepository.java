package org.mgoulene.repository;

import java.util.List;
import java.util.Optional;
import org.mgoulene.domain.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BankAccount entity.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {
    default Optional<BankAccount> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BankAccount> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BankAccount> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct bankAccount from BankAccount bankAccount left join fetch bankAccount.account",
        countQuery = "select count(distinct bankAccount) from BankAccount bankAccount"
    )
    Page<BankAccount> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct bankAccount from BankAccount bankAccount left join fetch bankAccount.account")
    List<BankAccount> findAllWithToOneRelationships();

    @Query("select bankAccount from BankAccount bankAccount left join fetch bankAccount.account where bankAccount.id =:id")
    Optional<BankAccount> findOneWithToOneRelationships(@Param("id") Long id);
}
