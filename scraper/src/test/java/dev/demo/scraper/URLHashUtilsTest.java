package dev.demo.scraper;

import dev.demo.scraper.utils.URLHashUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class URLHashUtilsTest {

    @Test
    void when_compare_urls_then_hash_is_equal() {
        String firstUrl = "www.example.com/?foo=bar&hello=world";
        String secondUrl = "http://www.example.com/?foo=bar&hello=world";
        String thirdUrl = "http://www.example.com/?hello=world&foo=bar";

        String firstHash = URLHashUtils.hash(firstUrl);
        String secondHash = URLHashUtils.hash(secondUrl);
        String thirdHash = URLHashUtils.hash(thirdUrl);

        assertEquals(firstHash, secondHash);
        assertEquals(firstHash, thirdHash);
    }
}
