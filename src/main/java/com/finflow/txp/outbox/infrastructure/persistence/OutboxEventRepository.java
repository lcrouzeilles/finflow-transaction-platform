package com.finflow.txp.outbox.infrastructure.persistence;

import com.finflow.txp.outbox.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    List<OutboxEventEntity> findTop50ByStatusAndNextAttemptAtBeforeOrderByCreatedAtAsc(OutboxStatus status, Instant now);
}
