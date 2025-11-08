package com.news.feed.bot.exception;

public class InvalidFetchedArticleException extends RuntimeException {
    public InvalidFetchedArticleException(String message) {
        super(message);
    }
}
