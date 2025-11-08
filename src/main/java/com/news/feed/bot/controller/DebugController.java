package com.news.feed.bot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.*;

@RestController
public class DebugController {

    private final OpenAiChatModel model;

    public DebugController(OpenAiChatModel model) {
        this.model = model;
    }

    @GetMapping("/debug/openai")
    public String debugModel() {
        return "Using model: " + model.getDefaultOptions().getModel() +
                ", temperature: " + model.getDefaultOptions().getTemperature() +
                ", max tokens: " + model.getDefaultOptions().getMaxTokens();
    }
}
