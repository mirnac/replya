package com.replya.component;

import tools.jackson.databind.JsonNode;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-28
 */
public interface MessageHandler {
    void handle(JsonNode payload);
}
