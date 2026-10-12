package com.lipari.bank.account;

import com.lipari.bank.account.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIban(String iban);

    Page<Account> findAllByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.customer.id = :customerId")
    Optional<BigDecimal> sumBalanceByCustomerId(@Param("customerId") Long customerId);
}
