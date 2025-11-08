package com.news.feed.bot.service.summarizer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OpenAIService implements Summarizer {

    private final String prompt;
    private final ChatClient chatClient;

    public OpenAIService(ChatClient.Builder builder, @Value("${openai.prompt}") String prompt) {
        this.chatClient = builder.build();
        this.prompt = prompt;
    }

    public String trySummarize(String text) {
        String summary = chatClient.prompt()
                .system(prompt)
                .user(text)
                .call() // отправляет запрос
                .content(); // вытаскивает поле content из ответа

        log.info("Received summary from Open AI API: {}", summary);
        return summary;
    }

    @Override
    public String summarize(String text) {
        try {
            return trySummarize(text);
        } catch (Exception e) {
            log.error("Error to summarize using Open AI API. Error: {}.", e.getMessage());
            return smartTrim(text);
        }
    }

    private String smartTrim(String text) {
        String[] sentences = text.split("[.!?]");
        StringBuilder sb = new StringBuilder();
        int words = 0;
        for (String s : sentences) {
            String[] w = s.trim().split("\\s+");
            if (words + w.length > 80) {
                break;
            }
            if (words > 0) {
                sb.append(". ");
            }
            sb.append(s.trim());
            words += w.length;
        }
        return sb.append(".").toString();
    }
}