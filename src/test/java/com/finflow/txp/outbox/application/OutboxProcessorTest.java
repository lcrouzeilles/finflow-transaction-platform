package com.finflow.txp.outbox.application;

import com.finflow.txp.outbox.domain.OutboxStatus;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventEntity;
import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxProcessorTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private KafkaOutboxPublisher kafkaOutboxPublisher;

    @InjectMocks
    private OutboxProcessor outboxProcessor;

    @Test
    void shouldMarkEventSentAfterSuccessfulPublish() throws Exception {
        OutboxEventEntity event = OutboxEventEntity.pending("TRANSFER", UUID.randomUUID(), "transfer.created.v1", "topic", "{}");
        when(outboxEventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        outboxProcessor.publishOne(event.getId());

        verify(kafkaOutboxPublisher).publish(event);
        verify(outboxEventRepository).save(event);
        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
    }
}
