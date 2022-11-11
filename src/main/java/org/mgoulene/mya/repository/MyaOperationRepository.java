package org.mgoulene.mya.repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import org.mgoulene.domain.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Operation entity.
 */
@Repository
public interface MyaOperationRepository extends JpaRepository<Operation, Long>, JpaSpecificationExecutor<Operation> {
    @Query(
        "SELECT operation FROM Operation operation where operation.date = :date AND FLOOR(operation.amount) = FLOOR(:amount) AND operation.label = :label AND operation.account.id = :accountId AND operation.isUpToDate = false"
    )
    List<Operation> findAllByDateAmountLabelAccountAndNotUpToDate(
        @Param("date") LocalDate date,
        @Param("amount") float amount,
        @Param("label") String label,
        @Param("accountId") Long accountId
    );

    @Modifying
    @Query("UPDATE Operation operation SET operation.isUpToDate = false WHERE operation.account.id = :accountId")
    int updateIsUpToDate(@Param("accountId") Long accountId);

    @Modifying
    @Query("DELETE FROM Operation operation WHERE operation.account.id = :accountId AND operation.isUpToDate = false")
    int deleteIsNotUpToDate(@Param("accountId") Long accountId);

    @Query(
        "SELECT operation FROM Operation operation WHERE operation.account.id = :accountId AND operation.subCategory.category.id = :categoryId AND ABS((operation.amount - :amount)/operation.amount) < 0.2 AND operation.date > :dateFrom AND operation.date < :dateTo "
    )
    List<Operation> findAllCloseToBudgetItemPeriod(
        @Param("accountId") Long accountId,
        @Param("categoryId") Long categoryId,
        @Param("amount") float amount,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );

    @Query(
        "SELECT operation FROM Operation operation WHERE operation.account.id = :accountId AND operation.subCategory.category.id = :categoryId AND ABS((operation.amount - :amount)/operation.amount) < 0.2 AND operation.date > :dateFrom AND operation.date < :dateTo AND NOT operation IN (SELECT bip.operation FROM BudgetItemPeriod bip WHERE bip.operation.id IS NOT NULL )"
    )
    List<Operation> findAllCloseToBudgetItemPeriodWithoutAlreadyAssigned(
        @Param("accountId") Long accountId,
        @Param("categoryId") Long categoryId,
        @Param("amount") float amount,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );

    @Query("SELECT MAX(operation.date) FROM Operation operation where operation.account.id = :accountId")
    LocalDate findLastOperationDate(@Param("accountId") Long accountId);

    @Query(
        "SELECT operation FROM Operation operation left join fetch operation.subCategory " +
        "left join fetch operation.account " +
        "left join fetch operation.budgetItemPeriod " +
        "where operation.account.id = :accountId " +
        "order by operation.date desc"
    )
    List<Operation> findAllByAccount(@Param("accountId") Long accountId);

    @Query("SELECT SUM(operation.amount) FROM Operation operation " + "where operation.bankAccount.id = :bankAccountId ")
    Float getSumOfOperationForBankAccount(@Param("bankAccountId") Long bankAccountId);

    @Query("SELECT MAX(operation.date) FROM Operation operation " + "where operation.bankAccount.id = :bankAccountId ")
    LocalDate getLastOperationDateForBankAccount(@Param("bankAccountId") Long bankAccountId);
}
