package com.news.feed.bot.service.feed;

import com.news.feed.bot.model.Article;

import java.util.List;

public interface FeedService {
    List<Article> getFeed(String feedUrl);
}
