package dev.demo.scraper.service;

import dev.demo.scraper.exception.LinkException;
import dev.demo.scraper.model.PageDetails;
import dev.demo.scraper.model.Suggestion;
import dev.demo.scraper.model.jpa.Link;
import dev.demo.scraper.repository.LinkRepository;
import dev.demo.scraper.utils.Analyzer;
import dev.demo.scraper.utils.URLHashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Slf4j
@Service
public class SuggestionsService {

    private final SuggestionsCache suggestionsCache;

    private final LinkRepository linkRepository;

    public Suggestion createSuggestion(String userId, String url) throws ExecutionException, InterruptedException {

        CompletableFuture<PageDetails> pageDetailsFuture = doAnalyse(url);

        String hash = URLHashUtils.hash(url);
        Optional<Link> linkOptional = linkRepository.findAllByUserIdAndAndHash(userId, hash);
        if (linkOptional.isPresent()) {
            pageDetailsFuture.cancel(true);
            throw new LinkException(LinkException.Message.DUPLICATE_LINK);
        } else {
            List<String> tagsByPopularity = linkRepository.findTagsByPopularity(hash);
            Set<String> tags = new LinkedHashSet<>(tagsByPopularity);
            PageDetails pageDetails = pageDetailsFuture.get();

            Suggestion suggestion = new Suggestion(url, URLHashUtils.hash(url), pageDetails.getTitle(), pageDetails.getKeywords(), tags);
            suggestionsCache.put(userId, suggestion);
            return suggestion;
        }
    }

    @Async
    CompletableFuture<PageDetails> doAnalyse(String url) {
        try {
            PageDetails pageDetails = Analyzer.analysePage(url);
            return CompletableFuture.completedFuture(pageDetails);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

}
