package com.news.feed.bot.service.bot;

import com.news.feed.bot.exception.SourceNotFoundException;
import com.news.feed.bot.model.Source;
import com.news.feed.bot.service.source.SourceService;
import com.news.feed.bot.util.Utils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TelegramUpdatesListener {

    private final TelegramBot bot;
    private final SourceService sourceService;
    private final Map<Long, Step> flow = new HashMap<>();

    public TelegramUpdatesListener(TelegramBot bot, SourceService sourceService) {
        this.bot = bot;
        this.sourceService = sourceService;

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                if (update.message() != null && update.message().text() != null) {
                    String message = update.message().text();
                    Long chatID = update.message().chat().id();
                    log.info("Bot received update! chatID: {}, message: {}", chatID, message);
                    String reply = getReply(message, chatID);
                    sendReply(bot, chatID, reply);
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private static void sendReply(TelegramBot bot, Long chatId, String text) {
        SendResponse response = bot.execute(
                new SendMessage(chatId, text).parseMode(ParseMode.MarkdownV2)
        );
        if (response.isOk()) {
            log.info("Sent: {}", text);
        } else {
            log.error("Error: {}", response.description());
        }
    }

    private String getReply(String message, Long chatID) {
        StringBuilder reply = new StringBuilder();

        if (message.equals("/sources")) {
            List<Source> sources = sourceService.getAll();
            if (sources.isEmpty()) {
                reply.append("Empty.");
            }
            for (Source source : sources) {
                reply.append(
                        String.format("Name: %s\nURL: %s\n\n", source.getName(), source.getFeedUrl())
                );
            }
            return "*Sources\\:*\n%s".formatted(Utils.escape(reply.toString()));
        } else if (message.equals("/start")) {
            reply.append("Welcome to Java News Feed Bot!\n\n");
            reply.append("Available commands: /sources");
            return Utils.escape(reply.toString());
        } else if (message.equals("/add_source")) {
            Step newStep = new Step();
            newStep.setState(StepState.WAITING_ADD_SOURCE_NAME);
            flow.put(chatID, newStep);
            reply.append("Send *source name*\\:");
            return reply.toString();
        } else if (message.equals("/remove_source")) {
            Step newStep = new Step();
            newStep.setState(StepState.WAITING_REMOVE_SOURCE_URL);
            flow.put(chatID, newStep);
            reply.append("Send *source name*\\:");
            return reply.toString();
        }

        Step step = flow.get(chatID);
        if (step == null) {
            reply.append("Available commands: /sources");
            return reply.toString();
        }

        switch (step.getState()) {
            case WAITING_REMOVE_SOURCE_URL:
                try {
                    Source source = sourceService.removeByUrl(message);
                    flow.remove(chatID);
                    reply.append(
                            String.format("Removed source\\! Name: %s, URL: %s", Utils.escape(source.getName()), Utils.escape(source.getFeedUrl()))
                    );
                    return reply.toString();
                } catch (SourceNotFoundException e) {
                    reply.append(e.getMessage());
                    return reply.toString();
                }
            case WAITING_ADD_SOURCE_NAME:
                step.setState(StepState.WAITING_ADD_SOURCE_URL);
                step.setSourceName(message);
                reply.append("Send *feed url*\\:");
                return reply.toString();
            case WAITING_ADD_SOURCE_URL:
                String sourceName = step.getSourceName();
                Source source = Source.builder()
                        .name(sourceName)
                        .feedUrl(message)
                        .build();
                log.info("Creating new source using telegram! Name: {}, URL: {}", sourceName, message);
                source = sourceService.create(source);
                flow.remove(chatID);
                reply.append(
                        String.format("Created source\\! Name: %s, URL: %s", Utils.escape(source.getName()), Utils.escape(source.getFeedUrl()))
                );
                return reply.toString();
            default:
                break;
        }

        return reply.toString();
    }

    enum StepState {
        WAITING_ADD_SOURCE_URL,
        WAITING_ADD_SOURCE_NAME,
        WAITING_REMOVE_SOURCE_URL,
        UNDEFINED;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Step {
        StepState state = StepState.UNDEFINED;
        String sourceName;
        String sourceUrl;
    }
}
