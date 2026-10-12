package com.lipari.bank.transfer;

import com.lipari.bank.account.AccountRepository;
import com.lipari.bank.account.model.Account;
import com.lipari.bank.shared.exception.BusinessException;
import com.lipari.bank.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountRepository accountRepository;

    @Transactional
    public void executeTransfer(Long sourceId, Long destinationId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account source = accountRepository.findById(sourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found: " + sourceId));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient balance on account: " + sourceId);
        }

        Account destination = accountRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found: " + destinationId));

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);
    }
}
