package com.news.feed.bot.service.fetcher;

import com.news.feed.bot.model.Article;
import com.news.feed.bot.model.Source;
import com.news.feed.bot.service.article.ArticleService;
import com.news.feed.bot.service.feed.FeedService;
import com.news.feed.bot.service.source.SourceService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FetcherServiceImpl implements FetcherService {

    private final ArticleService articleService;
    private final SourceService sourceService;
    private final FeedService feedService;

    public FetcherServiceImpl(
            ArticleService articleService,
            SourceService sourceService,
            FeedService feedService
    ) {
        this.articleService = articleService;
        this.sourceService = sourceService;
        this.feedService = feedService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12) // 12hr
    @Async
    @Override
    public void FetchAndSave() {
        List<Source> sources = sourceService.getAll();

        for (Source source : sources) {
            List<Article> articles = feedService.getFeed(source.getFeedUrl());
            for (Article article : articles) {
                articleService.create(article);
            }
        }
    }
}
