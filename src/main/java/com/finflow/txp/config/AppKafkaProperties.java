package com.finflow.txp.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.kafka")
@Validated
public record AppKafkaProperties(
        @NotBlank String transferCreatedTopic,
        @NotBlank String transferCompletedTopic
) {
}
