package dev.demo.scraper.web;

import dev.demo.scraper.service.LinksService;
import dev.demo.scraper.web.dto.LinkRequest;
import dev.demo.scraper.web.dto.LinkResponse;
import dev.demo.scraper.model.jpa.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/links")
public class LinksController {

    private final LinksService linksService;

    public LinksController(LinksService linksService) {
        this.linksService = linksService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public LinkResponse createLink(@RequestBody LinkRequest linkRequest, @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserId(jwt);
        Link link = linksService.createLink(linkRequest.toLink(userId));
        return LinkResponse.toResponse(link);
    }

    private String getUserId(Jwt jwt) {
        return (String) jwt.getClaims().get("sub");
    }

    @GetMapping(path = "/all")
    @ResponseStatus(code = HttpStatus.OK)
    public Page<LinkResponse> getAllLinks(@RequestParam(required = false, defaultValue = "") String filter,
                                          @RequestParam(required = false, defaultValue = "0") Integer page,
                                          @RequestParam(required = false, defaultValue = "5") Integer size,
                                          @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserId(jwt);
        Pageable pageable = PageRequest.of(page, size);
        Page<Link> linkPage;
        if (filter.isEmpty()) {
            linkPage = linksService.getAllLinks(userId, pageable);
        } else {
            linkPage = linksService.getLinksFiltered(userId, filter, pageable);
        }

        List<LinkResponse> responseList = linkPage.stream()
                .map(LinkResponse::toResponse)
                .collect(Collectors.toList());


        return new PageImpl<>(responseList);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable("id") Long id, @AuthenticationPrincipal Jwt jwt) {
        String userId = getUserId(jwt);
        linksService.deleteLink(userId, id);
    }

}
