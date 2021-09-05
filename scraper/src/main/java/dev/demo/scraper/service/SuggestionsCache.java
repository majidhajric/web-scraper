package dev.demo.scraper.service;

import dev.demo.scraper.model.Suggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class SuggestionsCache {

    private final Map<String, Suggestion> cacheMap;

    public void put(String userId, Suggestion suggestion) {
        cacheMap.put(userId, suggestion);
    }

    public Suggestion getAndRemove(String userId) {
        Suggestion suggestion = cacheMap.get(userId);
        cacheMap.remove(userId);
        return suggestion;
    }
}
