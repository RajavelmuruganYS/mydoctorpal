package com.hospital.ehr.auth.repository;

import com.hospital.ehr.auth.entity.OtpVerification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OtpVerificationRepository
        extends JpaRepository<OtpVerification, Long> {

    @Modifying
    @Query("""
            UPDATE OtpVerification o
            SET o.verifiedAt = CURRENT_TIMESTAMP
            WHERE o.user.id = :userId
            AND o.purpose = :purpose
            AND o.verifiedAt IS NULL
            """)
    void invalidatePreviousOtps(
            @Param("userId") Long userId,
            @Param("purpose") String purpose
    );

    Optional<OtpVerification>
    findTopByUserIdAndPurposeOrderByCreatedAtDesc(
            Long userId,
            String purpose
    );

    @Query("""
            SELECT o
            FROM OtpVerification o
            WHERE o.user.id = :userId
            AND o.purpose = :purpose
            AND o.verifiedAt IS NULL
            ORDER BY o.createdAt DESC
            """)
    Optional<OtpVerification> findLatestValidOtpOptional(
            @Param("userId") Long userId,
            @Param("purpose") String purpose
    );

    default OtpVerification findLatestValidOtp(
            Long userId,
            String purpose
    ) {

        return findLatestValidOtpOptional(
                userId,
                purpose
        ).orElse(null);
    }
}