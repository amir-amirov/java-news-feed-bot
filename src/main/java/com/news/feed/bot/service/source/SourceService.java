package com.news.feed.bot.service.source;

import com.news.feed.bot.model.Source;
import com.news.feed.bot.repository.SourceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SourceService {
    private final SourceRepository sourceRepository;

    public SourceService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    public Source create(Source s) {
        return sourceRepository.save(s);
    }

    public List<Source> getAll() {
        return sourceRepository.findAll();
    }
}
