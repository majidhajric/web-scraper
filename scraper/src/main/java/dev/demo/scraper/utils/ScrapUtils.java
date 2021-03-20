package dev.demo.scraper.utils;

import dev.demo.scraper.model.PageDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ScrapUtils {

    public static PageDetails scrap(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        String title = document.getElementsByTag("title").text();
        String body = document.getElementsByTag("article").text();
        if (body.isEmpty()) {
            body = document.getElementsByTag("body").text();
        }
        return new PageDetails(title, body);
    }
}
