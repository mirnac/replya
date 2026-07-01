package com.replya.component;

import com.replya.Constants;
import com.replya.facade.ConversationOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-28
 * Json Payload Example
 *     { "object": "whatsapp_business_account",
 *           "entry": [
 *             { "id": "102290129340398",
 *               "changes": [
 *                 {
 *                   "value": {
 *                     "messaging_product": "whatsapp",
 *                     "metadata": {
 *                       "display_phone_number": "15550783881",
 *                       "phone_number_id": "106540352242922"
 *                     },
 *                     "contacts": [
 *                       {
 *                         "profile": {"name": "Sheena Nelson"},
 *                         "wa_id": "16505551234"
 *                       }
 *                     ],
 *                     "messages": [
 *                       {
 *                         "from": "16505551234",
 *                         "id": "wamid.HBgLMTY1MDM4Nzk0MzkVAgASGBQzQTRBNjU5OUFFRTAzODEwMTQ0RgA=",
 *                         "timestamp": "1749416383",
 *                         "type": "text",
 *                         "text": {"body": "Does it come in another color?"}
 *                       }
 *                     ]
 *                   },
 *                   "field": "messages"
 *                 }
 *               ]
 *             }
 *           ]
 *         }
 *
 */

@Component
public class MessageHandlerImpl implements MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerImpl.class);

    private final MessageSender sender;
    private final ConversationOrchestrator orchestrator;

    public MessageHandlerImpl(final MessageSender sender,
                              final ConversationOrchestrator orchestrator) {
        this.sender = sender;
        this.orchestrator = orchestrator;
    }


    @Async
    public void handle(JsonNode payload) {
        JsonNode value = payload
                .path("entry").path(0)
                .path("changes").path(0)
                .path("value");

        // Meta manda dos tipos de evento al mismo webhook:
        //   - mensajes entrantes -> value.messages
        //   - estados de envío    -> value.statuses (sent/delivered/read)


        // Por ahora, solo nos importan los entrantes. Prevenimos NPE.
        JsonNode messages = value.path("messages");
        if (!messages.isArray() || messages.isEmpty()) {
            return; // era un status u otro evento -> ignorar
        }

        // phone_number_id = identidad del tenant Y número DESDE el que respondemos
        String phoneNumberId = value.path("metadata").path("phone_number_id").asString();

        JsonNode message = messages.get(0);
        String from = message.path("from").asString(); // wa_id del cliente
        String type = message.path("type").asString();
        String wamid = message.path("id").asString(null);

        if (!Constants.MESSAGE_TYPE_TEXT.equals(type)) {
            sender.sendText(phoneNumberId, from, "Por ahora solo leo mensajes de texto");
            return;
        }

        String text = message.path("text").path("body").asString();
        LOGGER.info("Mensaje de {} (tenant {}): {}", from, phoneNumberId, text);

        orchestrator.handleIncoming(phoneNumberId, from, text, wamid);
    }
}
