package dev.demo.scraper.dto;

import dev.demo.scraper.model.jpa.Link;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class LinkResponse implements Serializable {

    private Long id;

    private LocalDateTime timeCreated;

    private String url;

    private String title;

    private Set<String> tags;

    public static LinkResponse toResponse(Link link) {
        LinkResponse response = new LinkResponse();
        response.id = link.getId();
        response.timeCreated = link.getCreatedAt();
        response.url = link.getUrl();
        response.title = link.getTitle();
        response.tags = link.getTags();
        return response;
    }
}
