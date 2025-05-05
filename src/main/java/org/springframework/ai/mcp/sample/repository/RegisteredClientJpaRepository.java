package org.springframework.ai.mcp.sample.repository;

import org.springframework.ai.mcp.sample.entity.RegisteredClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegisteredClientJpaRepository extends JpaRepository<RegisteredClientEntity, String> {
    Optional<RegisteredClientEntity> findByClientId(String clientId);
    void deleteByClientId(String clientId);
} 