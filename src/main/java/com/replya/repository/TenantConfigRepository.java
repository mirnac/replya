package com.replya.repository;

import com.replya.domain.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {
}
