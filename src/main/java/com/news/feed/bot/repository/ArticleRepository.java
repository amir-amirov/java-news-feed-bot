package com.news.feed.bot.repository;

import com.news.feed.bot.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop1ByPostedAtIsNullOrderByPublishedAtDesc();

    Optional<Article> findByUrl(String url);
}
