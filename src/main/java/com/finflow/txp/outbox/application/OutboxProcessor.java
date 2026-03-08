package com.finflow.txp.outbox.application;

import com.finflow.txp.outbox.domain.OutboxStatus;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventEntity;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaOutboxPublisher kafkaOutboxPublisher;

    public OutboxProcessor(OutboxEventRepository outboxEventRepository, KafkaOutboxPublisher kafkaOutboxPublisher) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaOutboxPublisher = kafkaOutboxPublisher;
    }

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:3000}")
    public void publishPendingEvents() {
        List<OutboxEventEntity> events = outboxEventRepository.findTop50ByStatusAndNextAttemptAtBeforeOrderByCreatedAtAsc(
                OutboxStatus.PENDING, Instant.now());
        for (OutboxEventEntity event : events) {
            publishOne(event.getId());
        }
    }

    @Transactional
    public void publishOne(UUID eventId) {
        OutboxEventEntity event = outboxEventRepository.findById(eventId).orElse(null);
        if (event == null || event.getStatus() != OutboxStatus.PENDING) {
            return;
        }

        try {
            kafkaOutboxPublisher.publish(event);
            event.markSent();
        } catch (Exception ex) {
            log.warn("Failed to publish outbox event {}", eventId, ex);
            event.markFailure(ex.getMessage());
        }

        outboxEventRepository.save(event);
    }
}
