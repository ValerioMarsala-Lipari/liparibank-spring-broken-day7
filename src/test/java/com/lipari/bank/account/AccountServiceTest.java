package com.lipari.bank.account;

import com.lipari.bank.account.model.Account;
import com.lipari.bank.customer.model.Customer;
import com.lipari.bank.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private AccountService accountService;

  @Test
  void findById_accountFound_returnsAccount() {
    Account account = new Account(
        1L,
        "IT60X054280001000000123456",
        BigDecimal.valueOf(500),
        null
    );

    when(accountRepository.findById(1L))
        .thenReturn(Optional.of(account));

    Account result = accountService.findById(1L);

    assertThat(result).isSameAs(account);
    verify(accountRepository).findById(1L);
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  void findById_accountNotFound_throwsResourceNotFoundException() {
    when(accountRepository.findById(99L))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> accountService.findById(99L)
    );

    verify(accountRepository).findById(99L);
    verifyNoMoreInteractions(accountRepository);
  }

  @Test
  void create_validAccount_savesAndReturnsAccount() {
    Account account = new Account(
        null,
        "IT60X054280001000000123456",
        BigDecimal.valueOf(500),
        new Customer(1L, "Mario Rossi", "mario@liparibank.it")
    );

    when(accountRepository.save(account)).thenReturn(account);

    Account result = accountService.create(account);

    assertThat(result).isSameAs(account);
    verify(accountRepository).save(account);
    verifyNoMoreInteractions(accountRepository);
  }
}