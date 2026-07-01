package com.replya.facade;

import com.replya.service.ConversationService;
import com.replya.service.ai.ClaudeClient;
import com.replya.component.MessageSender;
import com.replya.component.ai.PromptBuilder;
import com.replya.domain.Tenant;
import com.replya.domain.TenantConfig;
import com.replya.repository.TenantRepository;
import com.replya.repository.TenantConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// ConversationOrchestrator.java  (whatsapp-gateway) — coordina, SIN @Transactional global
@Service
public class ConversationOrchestrator {
    private static final Logger log = LoggerFactory.getLogger(ConversationOrchestrator.class);

    private final TenantRepository tenantRepo;
    private final TenantConfigRepository configRepo;
    private final ConversationService conversations;
    private final PromptBuilder promptBuilder;
    private final ClaudeClient claude;
    private final MessageSender sender;

    public ConversationOrchestrator(final TenantRepository tenantRepo,
                                    TenantConfigRepository configRepo,
                                    ConversationService conversations, PromptBuilder promptBuilder,
                                    ClaudeClient claude, MessageSender sender) {
        this.tenantRepo = tenantRepo; this.configRepo = configRepo;
        this.conversations = conversations; this.promptBuilder = promptBuilder;
        this.claude = claude; this.sender = sender;
    }

    public void handleIncoming(String phoneNumberId, String customerWaNumber, String text, String wamid) {

        if (conversations.alreadyProcessed(wamid)) {
            log.debug("wamid {} ya procesado, ignoro", wamid);
            return;
        }

        Tenant tenant = tenantRepo.findByPhoneNumberId(phoneNumberId).orElse(null);
        if (tenant == null) { log.warn("No tenant found for phone_number_id {}", phoneNumberId); return; }
        if (!"active".equals(tenant.getStatus())) { log.info("Tenant {} en pausa", tenant.getId()); return; }

        TenantConfig cfg = configRepo.findById(tenant.getId())
                .orElseThrow(() -> new IllegalStateException("Tenant sin config: " + tenant.getId()));

        // historial previo + mensaje actual (en memoria, todavía sin persistir)
        List<ClaudeClient.Msg> messages = conversations.loadHistory(tenant.getId(), customerWaNumber);
        messages.add(new ClaudeClient.Msg("user", text));

        String system = promptBuilder.systemPrompt(tenant, cfg);
        String reply = claude.complete(system, messages);   // fuera de transacción

        conversations.persistExchange(tenant.getId(), customerWaNumber, text, wamid, reply); // tx corta
        sender.sendText(phoneNumberId, customerWaNumber, reply);
    }
}