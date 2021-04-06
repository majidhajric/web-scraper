package dev.demo.scraper.controller;

import dev.demo.scraper.controller.dto.SuggestionResponse;
import dev.demo.scraper.model.Suggestion;
import dev.demo.scraper.service.SuggestionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(path = "/api/suggestions")
public class SuggestionsController {

    private final SuggestionsService suggestionsService;

    public SuggestionsController(SuggestionsService suggestionsService) {
        this.suggestionsService = suggestionsService;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public SuggestionResponse getSuggestions(@RequestParam(name = "pageURL") String pageURL, @AuthenticationPrincipal Jwt jwt) throws IOException {
        String userId = jwt.getClaim("sub");
        Suggestion suggestion = suggestionsService.createSuggestion(userId, pageURL);

        return SuggestionResponse.toResponse(suggestion);
    }
}
