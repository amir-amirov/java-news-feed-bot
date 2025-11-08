package com.news.feed.bot.util;

import org.jsoup.Jsoup;

public class Utils {
    public static String removeHtmlTags(String html) {
        return Jsoup.parse(html).text();
    }
}
