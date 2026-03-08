package com.finflow.txp.outbox.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferCreatedEvent(
        UUID transferId,
        String tenantId,
        String clientReference,
        String sourceAccountId,
        String destinationAccountId,
        BigDecimal amount,
        String currency,
        String status,
        Instant createdAt
) {
}
