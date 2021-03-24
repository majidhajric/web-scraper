package dev.demo.scraper.model;

import dev.demo.scraper.model.jpa.Link;
import dev.demo.scraper.utils.URLHashUtils;
import lombok.Getter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class Suggestion {

    private String url;

    private String hash;

    private String title;

    private Set<String> keywords = new LinkedHashSet<>();

    private Set<String> tags = new LinkedHashSet<>();

    public Suggestion(String url, String hash, String title, Set<String> keywords, Set<String> tags) {
        this.url = url;
        this.hash = hash;
        this.title = title;
        this.keywords = new LinkedHashSet<>(keywords);
        this.tags = new LinkedHashSet<>(tags);
    }

    public boolean validate(Link link) {
        String urlHash = URLHashUtils.hash(link.getUrl());
        if (!this.hash.equals(urlHash)) {
            return false;
        }

        Set<String> suggestedTags = new HashSet<>();
        suggestedTags.addAll(this.keywords);
        suggestedTags.addAll(this.tags);

        return suggestedTags.containsAll(link.getTags());
    }
}
