package com.finflow.txp.outbox.infrastructure.persistence;

import com.finflow.txp.outbox.domain.OutboxStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events",
        indexes = {
                @Index(name = "idx_outbox_status_next_attempt", columnList = "status,next_attempt_at"),
                @Index(name = "idx_outbox_aggregate", columnList = "aggregate_type,aggregate_id")
        })
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 64)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 128)
    private String eventType;

    @Column(nullable = false, length = 128)
    private String topic;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "last_error", length = 512)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Version
    private Long version;

    public static OutboxEventEntity pending(String aggregateType, UUID aggregateId, String eventType, String topic, String payload) {
        OutboxEventEntity event = new OutboxEventEntity();
        event.id = UUID.randomUUID();
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.eventType = eventType;
        event.topic = topic;
        event.payload = payload;
        event.status = OutboxStatus.PENDING;
        event.attempts = 0;
        event.createdAt = Instant.now();
        event.nextAttemptAt = event.createdAt;
        return event;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailure(String errorMessage) {
        this.attempts = this.attempts + 1;
        this.lastError = errorMessage == null ? "unknown" : errorMessage.substring(0, Math.min(errorMessage.length(), 500));
        this.nextAttemptAt = Instant.now().plusSeconds(Math.min(300, attempts * 10L));
        if (this.attempts >= 8) {
            this.status = OutboxStatus.FAILED;
        }
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }
}
