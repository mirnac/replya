package com.replya.service;

import com.replya.domain.Conversation;
import com.replya.repository.ConversationRepository;
import com.replya.service.ai.ClaudeClient;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// ConversationService.java
@Service
public class ConversationService {
    private final ConversationRepository repo;
    private final int historySize;

    public ConversationService(ConversationRepository repo,
                               @Value("${replya.history-size:10}") int historySize) {
        this.repo = repo;
        this.historySize = historySize;
    }

    public boolean alreadyProcessed(String wamid) {
        return wamid != null && repo.existsByWamid(wamid);
    }

    public List<ClaudeClient.Msg> loadHistory(Long tenantId, String customerWaId) {
        var recent = repo.findRecent(tenantId, customerWaId, PageRequest.of(0, historySize));
        Collections.reverse(recent); // de más viejo a más nuevo
        return recent.stream()
                .map(c -> new ClaudeClient.Msg(c.getRole(), c.getContent()))
                .collect(Collectors.toList()); // mutable: le agrego el mensaje actual después
    }

    @Transactional
    public void persistExchange(Long tenantId, String customerWaId,
                                String userText, String userWamid, String assistantText) {
        repo.save(message(tenantId, customerWaId, "user", userText, userWamid));
        repo.save(message(tenantId, customerWaId, "assistant", assistantText, null));
    }

    private Conversation message(Long tid, String wa, String role, String content, String wamid) {
        var c = new Conversation();
        c.setTenantId(tid); c.setCustomerWaId(wa);
        c.setRole(role); c.setContent(content); c.setWamid(wamid);
        return c;
    }
}
