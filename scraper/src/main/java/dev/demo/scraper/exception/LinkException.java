package dev.demo.scraper.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LinkException extends RuntimeException {

    public LinkException(Message message) {
        super(message.getMessage());
    }

    @RequiredArgsConstructor
    public enum Message {
        INVALID_LINK("Invalid Link"),
        DUPLICATE_LINK("Link with same URL exists.");
        @Getter
        private final String message;
    }
}
