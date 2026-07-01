package com.replya.repository;

import com.replya.domain.Conversation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Mirna Cantero
 * Date: 2026-06-29
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    boolean existsByWamid(String wamid);

    @Query("select c from Conversation c " +
            "where c.tenantId = :tid and c.customerWaId = :wa " +
            "order by c.createdAt desc")
    List<Conversation> findRecent(@Param("tid") Long tid, @Param("wa") String wa, Pageable pageable);
}
