package dev.demo.scraper.dto;

import dev.demo.scraper.model.jpa.Link;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class LinkResponse implements Serializable {

    private Long id;

    private LocalDateTime createdAt;

    private String url;

    private String title;

    private Set<String> tags;

    public static LinkResponse toResponse(Link link) {
        LinkResponse response = new LinkResponse();
        response.setId(link.getId());
        response.setCreatedAt(link.getCreatedAt());
        response.setUrl(link.getUrl());
        response.setTitle(link.getTitle());
        response.setTags(link.getTags());
        return response;
    }
}
