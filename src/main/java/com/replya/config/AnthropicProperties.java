package com.replya.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// AnthropicProperties.java
@ConfigurationProperties(prefix = "anthropic")
public record AnthropicProperties(
        String apiKey, String baseUrl, String version, String model, int maxTokens) {}