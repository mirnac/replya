package com.replya.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
// TenantConfig.java  (el "cerebro" de cada cliente; se inyecta en el system prompt)
@Entity
@Table(name = "tenant_config", schema = "replya")
@Getter @Setter
@NoArgsConstructor
public class TenantConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uuid", nullable = false, unique = true, insertable = false, updatable = false)
    private UUID uuid;
    @Column(name = "tenant_id", nullable = false, unique = true)
    private Long tenantId;
    @Column(name = "bot_name", nullable = false)
    private String botName;
    @Column(nullable = false)
    private String tone;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private List<ServiceItem> services = new ArrayList<>();          // [{nombre, precio, duracion}]
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "business_hours", nullable = false)
    private Map<String, String> businessHours = new LinkedHashMap<>(); // {lun_vie, sab, dom}
    private String address;
    @Column(name = "maps_url")
    private String mapsUrl;
    @Column(name = "payment_info")
    private String paymentInfo;
    @Column(name = "extra_info")
    private String extraInfo;       // FAQ libre / aclaraciones
    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;
}
