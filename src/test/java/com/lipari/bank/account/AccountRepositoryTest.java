package com.lipari.bank.account;

import com.lipari.bank.account.model.Account;
import com.lipari.bank.customer.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = entityManager.persist(new Customer(null, "Mario Rossi", "mario@liparibank.it"));
        entityManager.flush();
    }

    @Test
    void findByIban_existingIban_returnsAccount() {
        entityManager.persist(
                new Account(null, "IT60X054280001000000123456", BigDecimal.valueOf(1000), customer));
        entityManager.flush();

        Optional<Account> found = accountRepository.findByIban("IT60X054280001000000123456");

        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void findByIban_nonExistingIban_returnsEmpty() {
        Optional<Account> found = accountRepository.findByIban("NONEXISTENT");

        assertThat(found).isEmpty();
    }

    @Test
    void findAllByCustomerId_returnsOnlyCustomerAccounts() {
        entityManager.persist(
                new Account(null, "IT60X054280001000000111111", BigDecimal.valueOf(500), customer));
        entityManager.persist(
                new Account(null, "IT60X054280001000000222222", BigDecimal.valueOf(300), customer));
        entityManager.flush();

        Page<Account> accounts = accountRepository.findAllByCustomerId(
                customer.getId(), PageRequest.of(0, 10));

        assertThat(accounts.getContent()).hasSize(2);
    }

    @Test
    void sumBalanceByCustomerId_returnsCorrectSum() {
        entityManager.persist(
                new Account(null, "IT60X054280001000000111111", BigDecimal.valueOf(500), customer));
        entityManager.persist(
                new Account(null, "IT60X054280001000000222222", BigDecimal.valueOf(300), customer));
        entityManager.flush();

        Optional<BigDecimal> sum = accountRepository.sumBalanceByCustomerId(customer.getId());

        assertThat(sum).isPresent();
        assertThat(sum.get()).isEqualByComparingTo(BigDecimal.valueOf(800));
    }
}
