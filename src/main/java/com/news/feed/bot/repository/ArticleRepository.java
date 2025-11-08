package com.news.feed.bot.repository;

import com.news.feed.bot.model.Article;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findTop1ByPostedAtIsNullOrderByPublishedAtDesc();

    Optional<Article> findByUrl(String url);
}
