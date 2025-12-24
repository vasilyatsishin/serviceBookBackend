package com.example.serviceBookBackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Створюємо простий менеджер кешу в пам'яті
        return new ConcurrentMapCacheManager("carPhotos", "carsList");
    }
}
