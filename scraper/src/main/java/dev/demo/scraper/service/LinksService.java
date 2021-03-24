package dev.demo.scraper.service;

import dev.demo.scraper.exception.LinkException;
import dev.demo.scraper.model.Suggestion;
import dev.demo.scraper.model.jpa.Link;
import dev.demo.scraper.repository.LinkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LinksService {

    private final LinkRepository linkRepository;

    private final SuggestionsCache suggestionsCache;

    public LinksService(LinkRepository linkRepository, SuggestionsCache suggestionsCache) {
        this.linkRepository = linkRepository;
        this.suggestionsCache = suggestionsCache;
    }

    public Link createLink(Link link) {

        Suggestion suggestion = suggestionsCache.getAndRemove(link.getUserId());

        if (suggestion == null || !suggestion.validate(link)) {
            throw new LinkException(LinkException.Message.INVALID_LINK);
        }

        return linkRepository.save(link);
    }

    public Page<Link> getAllLinks(String userId, Pageable pageable) {
        return linkRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Page<Link> getLinksFiltered(String userId, String tagFilter, Pageable pageable) {
        return linkRepository.findByUserIdAndTagContaining(userId, tagFilter, pageable);
    }

    public void deleteLink(String userId, Long linkId) {
        linkRepository.deleteAllByUserIdAndId(userId, linkId);
    }
}
