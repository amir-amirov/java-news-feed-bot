package com.news.feed.bot.service.feed;

import com.news.feed.bot.exception.InvalidFetchedArticleException;
import com.news.feed.bot.model.Article;
import com.news.feed.bot.util.Utils;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RSSService implements FeedService {

    @Override
    public List<Article> getFeed(String feedUrl) {
        List<Article> feed = new ArrayList<>();
        try {
            URL feedSource = new URL(feedUrl);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(feedSource));
            List<SyndEntry> entries = syndFeed.getEntries();
            for (SyndEntry entry : entries) {
                try {
                    Article article = parseFetchedArticle(entry);
                    String cleanContent = Utils.removeHtmlTags(article.getContent());
                    article.setContent(cleanContent);
                    feed.add(article);
                } catch (InvalidFetchedArticleException e) {
                    log.warn(e.getMessage());
                }
            }
        } catch (MalformedURLException e) {
            log.error("Invalid feed URL: {}", feedUrl);
        } catch (IOException e) {
            log.error("Could not parse XML for feed: {}", feedUrl);
        } catch (FeedException e) {
            log.error("Feed Error: {}", e.getMessage());
        }
        return feed;
    }

    private Article parseFetchedArticle(SyndEntry entry) {
        String url = entry.getLink();
        String title = entry.getTitle();
        List<SyndContent> entryContents = entry.getContents();
        SyndContent entryDescription = entry.getDescription();

        Date publishedAt = entry.getPublishedDate();

        String content = "";
        String description = "";

        if (url == null || url.isEmpty() || title == null || title.isEmpty() || publishedAt == null) {
            throw new InvalidFetchedArticleException("Invalid fetched article. No URL or title or publication date.");
        }

        if (entryContents != null && !entryContents.isEmpty() && !entryContents.get(0).getValue().isEmpty()) {
            content = entryContents.get(0).getValue();
        }
        if (entryDescription != null && !entryDescription.getValue().isEmpty()) {
            description = entryDescription.getValue();
        }

        if (content.isEmpty() && description.isEmpty()) {
            throw new InvalidFetchedArticleException("Invalid fetched article. No content of the article.");
        }

        return Article.builder()
                .url(url)
                .title(title)
                .publishedAt(publishedAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .content(content.length() > description.length() ? content : description)
                .build();
    }
}
