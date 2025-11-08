package com.news.feed.bot.service.publisher;

import com.news.feed.bot.model.Article;
import com.news.feed.bot.service.article.ArticleService;
import com.news.feed.bot.service.bot.BotService;
import com.news.feed.bot.service.summarizer.Summarizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PublisherServiceImpl implements PublisherService {

    private final ArticleService articleService;
    private final BotService botService;
    private final Summarizer summarizer;

    public PublisherServiceImpl(ArticleService articleService, BotService botService, Summarizer summarizer) {
        this.botService = botService;
        this.summarizer = summarizer;
        this.articleService = articleService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 12)
    @Override
    public void publish() {
        List<Article> articles = articleService.getNotPosted();
        for (Article article : articles) {
            log.info("Trying to publish article... Title: {} Url: {}", article.getTitle(), article.getUrl());
            String summary = summarizer.summarize(article.getContent());
            boolean isPosted = botService.makePost(article.getTitle(), summary, article.getUrl());
            if (isPosted) {
                log.info("Posted successfully! Title: {} Url: {}", article.getTitle(), article.getUrl());
                articleService.updatePostedAt(article);
            } else {
                log.info("Failed to post! Title: {} Url: {}", article.getTitle(), article.getUrl());
            }
        }
    }
}
