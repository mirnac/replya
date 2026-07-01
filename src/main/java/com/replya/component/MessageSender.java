package com.replya.component;

import com.replya.config.WhatsAppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-28
 */
@Component
public class MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private final RestClient restClient;
    private final WhatsAppProperties properties;

    public MessageSender(WhatsAppProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.graphBaseUrl())
                .build();
    }

    public void sendText(String phoneNumberId, String to, String body) {
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "text",
                "text", Map.of("preview_url", false, "body", body)
        );

        String url = "/" + properties.graphApiVersion() + "/" + phoneNumberId + "/messages";

        try {
            String response = restClient.post()
                    .uri(url)
                    .header("Authorization", "Bearer " + properties.accessToken())
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            logger.info("Enviado a {}: {}", to, response);

        } catch (Exception e) {
            logger.error("Error enviando a {}", to, e);
        }
    }
}
