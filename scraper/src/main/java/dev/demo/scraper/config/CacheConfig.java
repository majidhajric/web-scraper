package dev.demo.scraper.config;

import dev.demo.scraper.model.Suggestion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    @Bean
    public Map<String, Suggestion> suggestionMap() {
        return new ConcurrentHashMap<>();
    }

}
