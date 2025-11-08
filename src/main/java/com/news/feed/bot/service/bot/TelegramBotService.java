package com.news.feed.bot.service.bot;

import com.news.feed.bot.util.Utils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TelegramBotService implements BotService {

    private final TelegramBot telegramBot;
    private final String channelId;
    private final String adminID;

    public TelegramBotService(TelegramBot telegramBot, String channelId, @Value("${telegram.admin.id}") String adminID) {
        this.telegramBot = telegramBot;
        this.channelId = channelId;
        this.adminID = adminID;
    }

    @Override
    public boolean makePost(String title, String summary, String url) {
        String safeTitle = Utils.escape(title);
        String safeSummary = Utils.escape(summary);
        String safeUrl = Utils.escape(url);

        String postFormat = "*%s*\n\n%s\n\n%s";
        String post = postFormat.formatted(safeTitle, safeSummary, safeUrl);
        var response = telegramBot.execute(new SendMessage(channelId, post)
                        .parseMode(ParseMode.MarkdownV2));
        if (!response.isOk()) {
            log.error("Telegram error: {}. Error code: {}.", response.description(), response.errorCode());
        }
        return response.isOk();
    }

    @PostConstruct
    public void test() {
        var ok = telegramBot.execute(new SendMessage(adminID, "Bot is alive!"))
                .isOk();
        log.info("Test message sent → {}", ok);
    }


}
