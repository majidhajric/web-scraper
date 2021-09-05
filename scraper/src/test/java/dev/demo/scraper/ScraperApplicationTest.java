package dev.demo.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.demo.scraper.controller.dto.LinkRequest;
import dev.demo.scraper.controller.dto.SuggestionResponse;
import dev.demo.scraper.model.PageDetails;
import dev.demo.scraper.utils.Analyzer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ScraperApplicationTest {

    private static final String TITLE = "title";
    private static final String TEST_URL = "http://example.com/page";
    private final String firstUserId = UUID.randomUUID().toString();
    private final String secondUserId = UUID.randomUUID().toString();
    private final String thirdUserId = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    private static PageDetails createPageDetails() {
        Set<String> keywords = new LinkedHashSet<>(Arrays.asList("word1", "word2", "word3"));
        return new PageDetails(TITLE, keywords);
    }

    @BeforeAll
    static void setup_scrap_utils_mock() {
        MockedStatic<Analyzer> analyzerMockedStatic = Mockito.mockStatic(Analyzer.class);
        analyzerMockedStatic.when(() -> Analyzer.analysePage(anyString())).thenReturn(createPageDetails());
    }

    @WithMockUser
    @Test
    void when_create_duplicate_link_then_client_error() throws Exception {

        String content = mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords[0]").value("word1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords[1]").value("word2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords[2]").value("word3"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        SuggestionResponse response = mapper.readValue(content, SuggestionResponse.class);
        LinkRequest request = new LinkRequest();
        request.setUrl(TEST_URL);
        request.setTitle(TITLE);
        request.setTags(new HashSet<>(Arrays.asList("word1", "word2", "word3")));

        String jsonBody = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/links")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andExpect(status().isCreated())
                .andReturn();

        mockMvc.perform(post("/api/links")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @WithMockUser
    @Test
    void when_create_link_with_popular_tags_then_return_popular() throws Exception {
        String requestBody = "";

        String content = mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andReturn()
                .getResponse()
                .getContentAsString();
        SuggestionResponse firstUserResponse = mapper.readValue(content, SuggestionResponse.class);
        LinkRequest request = new LinkRequest();
        request.setUrl(firstUserResponse.getUrl());
        request.setTitle(TITLE);

        // first user
        request.setTags(new HashSet<>(Arrays.asList("word1", "word3")));
        requestBody = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/links")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andExpect(status().isCreated())
                .andReturn();

        // second user
        mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", secondUserId))))
                .andReturn();

        request.setTags(new HashSet<>(Arrays.asList("word2", "word3")));
        requestBody = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/links")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", secondUserId))))
                .andExpect(status().isCreated())
                .andReturn();

        // third user

        mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", thirdUserId))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[0]").value("word3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[1]").value("word1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags[2]").value("word2"))
                .andReturn();

    }


}
