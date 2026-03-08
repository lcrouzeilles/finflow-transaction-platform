package com.finflow.txp.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.security")
@Validated
public record AppSecurityProperties(
        @NotBlank String jwtSecret,
        String issuer
) {
}
