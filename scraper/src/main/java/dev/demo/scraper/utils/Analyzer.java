package dev.demo.scraper.utils;

import dev.demo.scraper.model.PageDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Analyzer {

    private static final int MIN_WORD_LENGTH = 3;
    private static final int MAX_RESULT_SIZE = 10;

    public static PageDetails analysePage(String url) throws IOException {
        Document document = Jsoup.connect(url).get();

        String title = document.getElementsByTag("title").text();
        String body = document.getElementsByTag("article").text();
        if (body.isEmpty()) {
            body = document.getElementsByTag("body").text();
        }
        return new PageDetails(title, analyseContent(body));
    }

    private static Set<String> analyseContent(String content) {
        Map<String, Integer> keywords = new HashMap<>();

        try (StandardAnalyzer analyzer = new StandardAnalyzer();
             TokenStream stream = analyzer.tokenStream("contents", new StringReader(content))) {
            TokenStream tokenStream = new ClassicFilter(stream);

            // remove english stop words
            tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());

            CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

            stream.reset();
            while (tokenStream.incrementToken()) {
                String term = token.toString();
                keywords.compute(term, (k, v) -> (v == null) ? 1 : v + 1);
            }
        } catch (Exception e) {
            log.error("Extraction Exception:", e);
        }

        return keywords.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .filter(word -> word.length() > MIN_WORD_LENGTH)
                .limit(MAX_RESULT_SIZE)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
