package dev.demo.scraper;

import dev.demo.scraper.utils.ContentAnalyzer;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ContentAnalyzerTest {

    private static final String CONTENT = "Tonight's big duel between Real Madrid and Barcelona has been played in front of two hundred people.\n" +
            "One hundred of people was from Barcelona, while another one hundred was from Real Madrid.\n" +
            "Ronaldo was player of the night. Ronaldo scored twice. Ronaldo was briliant.\n" +
            "Ronaldo provided fun for the people.\n" +
            "People were saying:Ronaldo,Ronaldo,Ronaldo...";

    @Test
    void whenContentAnalysed_thenKeywordsExtracted() {
        Set<String> keywords = ContentAnalyzer.analyse(CONTENT);
        assertThat(keywords.containsAll(Arrays.asList("Real Madrid", "Barcelona", "people")));
        assertThat(keywords.toArray()[0].equals("Ronaldo"));
    }

}
