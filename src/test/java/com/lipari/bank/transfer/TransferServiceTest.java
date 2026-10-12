package com.lipari.bank.transfer;

import com.lipari.bank.account.AccountRepository;
import com.lipari.bank.account.model.Account;
import com.lipari.bank.customer.model.Customer;
import com.lipari.bank.shared.exception.BusinessException;
import com.lipari.bank.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransferService transferService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer(1L, "Mario Rossi", "mario@liparibank.it");
    }

    @Test
    void executeTransfer_validTransfer_updatesBalances() {
        Account source = new Account(1L, "IT60X054280001000000123456", BigDecimal.valueOf(500), customer);
        Account destination = new Account(2L, "IT60X054280001000000654321", BigDecimal.valueOf(100), customer);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destination));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        transferService.executeTransfer(1L, 2L, BigDecimal.valueOf(200));

        assertThat(source.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
        assertThat(destination.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void executeTransfer_invalidAmount_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                transferService.executeTransfer(1L, 2L, BigDecimal.ZERO));
    }

    @Test
    void executeTransfer_insufficientBalance_throwsBusinessException() {
        Account sourceAccount = new Account(1L, "IT60X054280001000000123456", BigDecimal.valueOf(50), customer);
        Account destinationAccount = new Account(2L, "IT60X054280001000000654321", BigDecimal.valueOf(200), customer);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));

        assertThrows(BusinessException.class, () ->
                transferService.executeTransfer(1L, 2L, BigDecimal.valueOf(100)));
    }

    @Test
    void executeTransfer_sourceNotFound_throwsResourceNotFoundException() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                transferService.executeTransfer(1L, 2L, BigDecimal.valueOf(100)));
    }
}
