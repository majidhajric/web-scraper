package dev.demo.scraper.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PageDetails {

    private final String title;

    private final String content;
}
