package com.hospital.ehr.auth.repository;

import com.hospital.ehr.auth.entity.RefreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(
            String tokenHash
    );

    @Modifying
    @Query("""
            UPDATE RefreshToken r
            SET r.revokedAt = :revokedAt
            WHERE r.user.id = :userId
            AND r.revokedAt IS NULL
            """)
    void revokeAllTokensForUser(
            @Param("userId") Long userId,
            @Param("revokedAt") LocalDateTime revokedAt
    );
}