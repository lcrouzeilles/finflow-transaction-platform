package com.finflow.txp.transfer.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotBlank String clientReference,
        @NotBlank String sourceAccountId,
        @NotBlank String destinationAccountId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Pattern(regexp = "^[A-Z]{3}$") String currency,
        @NotBlank String beneficiaryId
) {
}
