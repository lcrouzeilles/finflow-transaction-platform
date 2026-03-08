package com.finflow.txp.transfer.application;

import com.finflow.txp.transfer.api.TransferResponse;
import com.finflow.txp.transfer.infrastructure.persistence.TransferEntity;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

    public TransferResponse toResponse(TransferEntity entity) {
        return new TransferResponse(
                entity.getId(),
                entity.getClientReference(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getRiskDecision(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
