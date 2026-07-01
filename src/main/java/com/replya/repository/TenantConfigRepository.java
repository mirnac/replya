package com.replya.repository;

import com.replya.domain.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {
    // tenant_config tiene su propio id surrogate; la config se busca por tenant_id, no por el PK.
    Optional<TenantConfig> findByTenantId(Long tenantId);
}
