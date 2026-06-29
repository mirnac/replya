package com.replya.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-28
 */
@ConfigurationProperties(prefix = "whatsapp.api")
public record WhatsAppProperties(
        String verifyToken,
        String accessToken,
        String graphBaseUrl,
        String graphApiVersion) {
}
