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
// Tenant.java  (tenant-config)
@Entity
@Table(name = "tenants", schema = "replya")
@Getter @Setter
@NoArgsConstructor
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uuid", nullable = false, unique = true, insertable = false, updatable = false)
    private UUID uuid;
    @Column(name = "phone_number_id", nullable = false, unique = true)
    private String phoneNumberId;
    @Column(name = "waba_id") private String wabaId;
    @Column(name = "display_name", nullable = false) private String displayName;
    private String ruc;
    private String status;
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
