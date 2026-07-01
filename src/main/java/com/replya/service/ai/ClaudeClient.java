package com.replya.service.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.replya.config.AnthropicProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// ClaudeClient.java
@Service
public class ClaudeClient {
    private final RestClient rc;
    private final AnthropicProperties props;

    public ClaudeClient(AnthropicProperties props) {
        this.props = props;
        this.rc = RestClient.builder().baseUrl(props.baseUrl()).build();
    }

    public String complete(String system, List<Msg> messages) {
        var body = new Request(props.model(), props.maxTokens(), system, messages);
        try {
            var resp = rc.post()
                    .uri("/v1/messages")
                    .header("x-api-key", props.apiKey())
                    .header("anthropic-version", props.version())
                    .header("content-type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(Response.class);
            return resp.content().stream()
                    .filter(b -> "text".equals(b.type()))
                    .map(Block::text)
                    .findFirst()
                    .orElse("Disculpá, no pude procesar tu mensaje. ¿Probás de nuevo?");
        } catch (Exception e) {
            throw new RuntimeException("Fallo llamando a Claude", e); // que reviente y reintente Meta
        }
    }

    public record Msg(String role, String content) {}
    record Request(String model, @JsonProperty("max_tokens") int maxTokens,
                   String system, List<Msg> messages) {}
    record Response(List<Block> content) {}
    record Block(String type, String text) {}
}
