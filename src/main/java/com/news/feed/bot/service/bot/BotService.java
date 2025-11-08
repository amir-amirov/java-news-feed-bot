package com.news.feed.bot.service.bot;

public interface BotService {
    boolean makePost(String title, String summary, String url);
}
