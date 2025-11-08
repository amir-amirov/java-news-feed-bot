package com.news.feed.bot.repository;

import com.news.feed.bot.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SourceRepository extends JpaRepository<Source, Long> {
    Optional<Source> findByFeedUrl(String feedUrl);
}
