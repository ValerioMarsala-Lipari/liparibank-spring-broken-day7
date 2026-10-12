package com.lipari.bank.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountRequest(
        @NotBlank String iban,
        @NotNull @Positive BigDecimal balance,
        @NotNull Long customerId
) {}
