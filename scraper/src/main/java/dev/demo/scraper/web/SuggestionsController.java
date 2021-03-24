package dev.demo.scraper.web;

import dev.demo.scraper.service.SuggestionsService;
import dev.demo.scraper.dto.SuggestionResponse;
import dev.demo.scraper.model.Suggestion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping(path = "/api/suggestions")
public class SuggestionsController {

    private final SuggestionsService suggestionsService;

    public SuggestionsController(SuggestionsService suggestionsService) {
        this.suggestionsService = suggestionsService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    public SuggestionResponse getSuggestions(@RequestParam(name = "pageURL") String pageURL, @AuthenticationPrincipal Jwt jwt)
            throws InterruptedException, IOException, URISyntaxException {
        String userId = jwt.getClaim("sub");
        Suggestion suggestion = suggestionsService.createSuggestion(userId, pageURL);

        return SuggestionResponse.toResponse(suggestion);
    }
}
