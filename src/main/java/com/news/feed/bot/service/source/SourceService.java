package com.news.feed.bot.service.source;

import com.news.feed.bot.exception.SourceNotFoundException;
import com.news.feed.bot.model.Source;
import com.news.feed.bot.repository.SourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SourceService {
    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public Source create(Source s) {
        try {
            return getByUrl(s.getFeedUrl());
        } catch (SourceNotFoundException e) {
            return sourceRepository.save(s);
        }
    }

    public Source getByUrl(String feedUrl) {
        Optional<Source> sourceOpt = sourceRepository.findByFeedUrl(feedUrl);
        if (sourceOpt.isEmpty()) {
            log.error("Source with feedUrl {} not found", feedUrl);
            throw new SourceNotFoundException("Source not found");
        }
        return sourceOpt.get();
    }

    public List<Source> getAll() {
        return sourceRepository.findAll();
    }

    public Source removeByUrl(String feedUrl) {
        Source source = getByUrl(feedUrl);
        sourceRepository.deleteById(source.getId());
        return source;
    }
}
