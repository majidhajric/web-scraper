package dev.demo.scraper.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentAnalyzer {

    public static Set<String> analyse(String content) {
        Map<String, Integer> keywords = new HashMap<>();

        // hack to keep dashed words (e.g. "non-specific" rather than "non" and "specific")
        String fixedContent = content.replaceAll("-+", "-0");
        // replace any punctuation char but dashes and apostrophes and by a space
        fixedContent = fixedContent.replaceAll("[\\p{Punct}&&[^'-]]+", " ");
        // replace most common english contractions
        fixedContent = fixedContent.replaceAll("(?:'(?:[tdsm]|[vr]e|ll))+\\b", "");

        try (StandardAnalyzer analyzer = new StandardAnalyzer();
             TokenStream stream = analyzer.tokenStream("contents", new StringReader(fixedContent))) {
            // to lower case
            TokenStream tokenStream = new ClassicFilter(stream);

            // remove english stop words
            tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.getDefaultStopSet());

            CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);

            stream.reset();
            // for each token
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
                .filter(word -> word.length() > 3)
                .limit(10)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
