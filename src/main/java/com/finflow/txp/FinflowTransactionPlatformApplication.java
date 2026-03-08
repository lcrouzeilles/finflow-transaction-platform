package com.finflow.txp;

import com.finflow.txp.config.AppKafkaProperties;
import com.finflow.txp.config.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableConfigurationProperties({AppKafkaProperties.class, AppSecurityProperties.class})
public class FinflowTransactionPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinflowTransactionPlatformApplication.class, args);
    }
}
