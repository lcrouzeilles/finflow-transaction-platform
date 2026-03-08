package com.finflow.txp.outbox.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finflow.txp.config.AppKafkaProperties;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventEntity;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventRepository;
import com.finflow.txp.transfer.infrastructure.persistence.TransferEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final AppKafkaProperties appKafkaProperties;

    public OutboxService(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper, AppKafkaProperties appKafkaProperties) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.appKafkaProperties = appKafkaProperties;
    }

    @Transactional
    public void enqueueTransferCreated(TransferEntity transfer) {
        TransferCreatedEvent event = new TransferCreatedEvent(
                transfer.getId(),
                transfer.getTenantId(),
                transfer.getClientReference(),
                transfer.getSourceAccountId(),
                transfer.getDestinationAccountId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getStatus().name(),
                transfer.getCreatedAt());

        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(OutboxEventEntity.pending(
                    "TRANSFER",
                    transfer.getId(),
                    "transfer.created.v1",
                    appKafkaProperties.transferCreatedTopic(),
                    payload));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize transfer.created event", ex);
        }
    }
}
