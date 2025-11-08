package com.news.feed.bot.service.article;

import com.news.feed.bot.model.Article;
import com.news.feed.bot.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article create(Article article) {
        Optional<Article> articleOpt = articleRepository.findByUrl(article.getUrl());
        if (articleOpt.isEmpty()) {
            return articleRepository.save(article);
        }
        return articleOpt.get();
    }

    public List<Article> getNotPosted() {
        return articleRepository.findTop1ByPostedAtIsNullOrderByPublishedAtDesc();
    }

    public void updatePostedAt(Article article) {
        article.setPostedAt(LocalDateTime.now());
        articleRepository.save(article);
    }
}
