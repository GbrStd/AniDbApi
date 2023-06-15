package com.example.anidbapi.animescraper;

public class FailedToScrapeException extends Exception {

    public FailedToScrapeException(String message) {
        super(message);
    }

    public FailedToScrapeException(String message, Throwable cause) {
        super(message, cause);
    }

}
