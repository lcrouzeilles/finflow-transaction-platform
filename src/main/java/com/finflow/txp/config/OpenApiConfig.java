package com.finflow.txp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI finflowOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finflow Transaction Platform API")
                        .version("v1")
                        .description("REST-first transaction processing platform with outbox-driven event publication"))
                .externalDocs(new ExternalDocumentation()
                        .description("Architecture")
                        .url("docs/ARCHITECTURE.md"));
    }
}
