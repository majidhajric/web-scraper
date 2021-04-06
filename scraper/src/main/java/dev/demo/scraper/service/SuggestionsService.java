package dev.demo.scraper.service;

import dev.demo.scraper.exception.LinkException;
import dev.demo.scraper.model.PageDetails;
import dev.demo.scraper.model.Suggestion;
import dev.demo.scraper.model.jpa.Link;
import dev.demo.scraper.repository.LinkRepository;
import dev.demo.scraper.utils.ContentAnalyzer;
import dev.demo.scraper.utils.ScrapUtils;
import dev.demo.scraper.utils.URLHashUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class SuggestionsService {

    private final SuggestionsCache suggestionsCache;

    private final LinkRepository linkRepository;

    public SuggestionsService(SuggestionsCache suggestionsCache,
                              LinkRepository linkRepository) {
        this.suggestionsCache = suggestionsCache;
        this.linkRepository = linkRepository;
    }

    public Suggestion createSuggestion(String userId, String url) throws IOException {
        String hash = URLHashUtils.hash(url);

        Optional<Link> linkOptional = linkRepository.findAllByUserIdAndAndHash(userId, hash);
        if (linkOptional.isPresent()) {
            throw new LinkException(LinkException.Message.DUPLICATE_LINK);
        }

        PageDetails pageDetails = ScrapUtils.scrap(url);
        Set<String> keywords = ContentAnalyzer.analyse(pageDetails.getContent());
        List<String> tagsByPopularity = linkRepository.findTagsByPopularity(hash);
        Set<String> tags = new LinkedHashSet<>(tagsByPopularity);

        Suggestion suggestion = new Suggestion(url, URLHashUtils.hash(url), pageDetails.getTitle(), keywords, tags);

        suggestionsCache.put(userId, suggestion);
        return suggestion;
    }
}
