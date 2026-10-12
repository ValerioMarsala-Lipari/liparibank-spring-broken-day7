package com.lipari.bank.account;

import com.lipari.bank.account.model.Account;
import com.lipari.bank.customer.model.Customer;
import com.lipari.bank.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @PreAuthorize("isAuthenticated()")
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }

    @PreAuthorize("isAuthenticated()")
    public Page<Account> findByCustomerId(Long customerId, Pageable pageable) {
        return accountRepository.findAllByCustomerId(customerId, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    public Optional<BigDecimal> getTotalBalanceByCustomer(Long customerId) {
        return accountRepository.sumBalanceByCustomerId(customerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ADVISOR')")
    @Transactional
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Long id) {
        Account account = findById(id);
        accountRepository.delete(account);
    }

    public Account save(Account account) {
        return accountRepository.save(account);
    }

    public Account findByIban(String iban) {
        return accountRepository.findByIban(iban)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with IBAN: " + iban));
    }

    public Customer buildCustomer(Long customerId) {
        Customer c = new Customer();
        c.setId(customerId);
        return c;
    }
}
