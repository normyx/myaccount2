package org.mgoulene.mya.repository;

import java.util.List;
import org.mgoulene.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MyaBankAccountRepository extends JpaRepository<BankAccount, Long>, JpaSpecificationExecutor<BankAccount> {
    @Query("select bank_account from BankAccount bank_account where bank_account.account.id = :accountId")
    List<BankAccount> findByAccountId(@Param("accountId") Long accountId);
}
