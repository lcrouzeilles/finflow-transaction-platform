package com.finflow.txp.outbox.application;

import com.finflow.txp.outbox.infrastructure.persistence.OutboxEventEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaOutboxPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaOutboxPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(OutboxEventEntity event) throws Exception {
        kafkaTemplate.send(event.getTopic(), event.getAggregateId().toString(), event.getPayload())
                .get(5, TimeUnit.SECONDS);
    }
}
