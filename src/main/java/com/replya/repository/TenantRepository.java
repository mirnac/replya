package com.replya.repository;

import com.replya.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByPhoneNumberId(String phoneNumberId);
}
