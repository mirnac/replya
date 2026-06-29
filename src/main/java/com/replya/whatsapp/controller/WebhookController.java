package com.replya.whatsapp.controller;

import com.replya.config.WhatsAppProperties;
import com.replya.whatsapp.component.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-28
 */
@RestController
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);


    private final WhatsAppProperties properties;
    private final MessageHandler messageHandler;

    public WebhookController(final WhatsAppProperties properties,
                             final MessageHandler messageHandler) {
        this.properties = properties;
        this.messageHandler = messageHandler;
    }

    //Verificación: Meta hace UN solo GET cuando guardás la Callback URL.
    //https://developers.facebook.com/documentation/business-messaging/whatsapp/webhooks/create-webhook-endpoint#request-syntax
    @GetMapping("/webhook")
    public ResponseEntity<String> verify(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        logger.info("Webhook verificando");
        if ("subscribe".equals(mode) && properties.verifyToken().equals(token)) {
            logger.info("Webhook verificado");
            return ResponseEntity.ok(challenge); // devolver el challenge tal cual, 200, texto plano
        }
        logger.warn("Verificación fallida: el verify token no coincide");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Recepción: mensajes entrantes Y estados de envío llegan a este mismo POST.
    //https://developers.facebook.com/documentation/business-messaging/whatsapp/webhooks/create-webhook-endpoint#post-requests
    @PostMapping("/webhook")
    public ResponseEntity<Void> receive(@RequestBody JsonNode payload) {
        // Devolver 200 lo antes posible: Meta reintenta si no recibe 200 en ~5s.
        try {
            messageHandler.handle(payload);
        } catch (Exception e) {
            logger.error("Error procesando webhook", e);
            // Igual devolvemos 200 para que Meta no entre en loop de reintentos por un bug nuestro.
        }
        return ResponseEntity.ok().build();
    }
}
