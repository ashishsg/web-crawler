package com.webcrawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Jsoup web crawler
 */
@Configuration
public class JsoupConfig {

    @Value("${crawler.user-agent:Mozilla/5.0 (compatible; WebCrawler/1.0)}")
    private String userAgent;

    @Value("${crawler.timeout:30000}")
    private int timeout;

    @Value("${crawler.follow-redirects:true}")
    private boolean followRedirects;

    @Value("${crawler.max-body-size:1048576}")
    private int maxBodySize;

    public String getUserAgent() {
        return userAgent;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public int getMaxBodySize() {
        return maxBodySize;
    }
}
