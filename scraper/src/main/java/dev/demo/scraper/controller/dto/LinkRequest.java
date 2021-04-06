package dev.demo.scraper.controller.dto;

import dev.demo.scraper.model.jpa.Link;
import dev.demo.scraper.utils.URLHashUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class LinkRequest implements Serializable {

    private String url;

    private String title;

    private Set<String> tags = new HashSet<>();

    public Link toLink(String userId) {
        return Link.builder()
                .userId(userId)
                .url(this.url)
                .hash(URLHashUtils.hash(this.url))
                .title(this.title)
                .createdAt(LocalDateTime.now())
                .tags(this.tags)
                .build();
    }

}
