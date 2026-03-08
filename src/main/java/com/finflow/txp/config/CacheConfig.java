package com.finflow.txp.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("beneficiaries", "accountProfiles", "pricingQuotes");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(Duration.ofMinutes(5)));
        return manager;
    }

    @Bean
    public CacheManagerCustomizer<CacheManager> cacheManagerCustomizer() {
        return cacheManager -> {
            // Placeholder so RedisCacheManager can replace the local cache manager in production if desired.
        };
    }
}
