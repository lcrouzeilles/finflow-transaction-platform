package com.finflow.txp.transfer.api;

import com.finflow.txp.transfer.domain.RiskDecision;
import com.finflow.txp.transfer.domain.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID transferId,
        String clientReference,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String currency,
        TransferStatus status,
        RiskDecision riskDecision,
        Instant createdAt,
        Instant updatedAt
) {
}
