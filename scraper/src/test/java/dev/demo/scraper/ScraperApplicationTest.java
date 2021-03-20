package dev.demo.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.demo.scraper.dto.LinkRequest;
import dev.demo.scraper.dto.SuggestionResponse;
import dev.demo.scraper.model.PageDetails;
import dev.demo.scraper.utils.ScrapUtils;
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
import java.util.LinkedList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ScraperApplicationTest {

    private static final String CONTENT = "Tonight's big duel between Real Madrid and Barcelona has been played in front of two hundred people.\n" +
            "One hundred of people was from Barcelona, while another one hundred was from Real Madrid.\n" +
            "Ronaldo was player of the night. Ronaldo scored twice. Ronaldo was briliant.\n" +
            "Ronaldo provided fun for the people.\n" +
            "People were saying:Ronaldo,Ronaldo,Ronaldo...";

    private static final String TITLE = "El-Classico";
    private static final String TEST_URL = "http://example.com/page";
    private final String firstUserId = UUID.randomUUID().toString();
    private final String secondUserId = UUID.randomUUID().toString();
    private final String thirdUserId = UUID.randomUUID().toString();
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    static void setup_scrap_utils_mock() {
        MockedStatic<ScrapUtils> scrapUtilsMockedStatic = Mockito.mockStatic(ScrapUtils.class);
        scrapUtilsMockedStatic.when(() -> ScrapUtils.scrap(anyString())).thenReturn(new PageDetails(TITLE, CONTENT));
    }

    @WithMockUser
    @Test
    void create_duplicate_link_test() throws Exception {

        String content = mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", firstUserId))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.keywords[0]").value("ronaldo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.tags").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        SuggestionResponse response = mapper.readValue(content, SuggestionResponse.class);
        LinkRequest request = new LinkRequest();
        request.setUrl(TEST_URL);
        request.setTitle(TITLE);
        request.setTags(response.getKeywords());

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
    void create_link_with_popular_tags_test() throws Exception {
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
        request.setTags(new HashSet<>(Arrays.asList("real", "people")));
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

        request.setTags(new HashSet<>(Arrays.asList("real", "madrid")));
        requestBody = mapper.writeValueAsString(request);

        mockMvc.perform(post("/api/links")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", secondUserId))))
                .andExpect(status().isCreated())
                .andReturn();

        // third user

        content = mockMvc.perform(get("/api/suggestions")
                .param("pageURL", TEST_URL)
                .with(jwt().jwt((jwt) -> jwt.claim("sub", thirdUserId))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SuggestionResponse thirdUserResponse = mapper.readValue(content, SuggestionResponse.class);

        assertThat(new LinkedList<String>(thirdUserResponse.getTags()).toArray()[0]).isEqualTo("real");
        assertThat(thirdUserResponse.getKeywords().containsAll(Arrays.asList("real", "people", "madrid"))).isTrue();
        assertThat(thirdUserResponse.getKeywords().containsAll(Arrays.asList("real", "barcelona", "madrid"))).isTrue();
        assertThat(thirdUserResponse.getKeywords().equals(firstUserResponse.getKeywords())).isTrue();

    }


}
