package com.replya.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// Conversation.java  (log de mensajes: user/assistant por tenant)
@Entity
@Table(name = "conversations", schema = "replya")
@Getter @Setter
@NoArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uuid", nullable = false, unique = true, insertable = false, updatable = false)
    private UUID uuid;
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;
    @Column(name = "customer_wa_id", nullable = false)
    private String customerWaId;    // número del cliente final
    @Column(nullable = false)
    private String role;            // 'user' | 'assistant'
    @Column(nullable = false)
    private String content;
    private String wamid;           // id de WhatsApp (dedup)
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
