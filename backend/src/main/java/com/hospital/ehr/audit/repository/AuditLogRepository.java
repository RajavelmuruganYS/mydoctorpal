package com.hospital.ehr.audit.repository;

import com.hospital.ehr.audit.entity.AuditLog;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);

    List<AuditLog> findByPatientOrderByCreatedAtDesc(Patient patient);

    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    List<AuditLog> findByResourceTypeOrderByCreatedAtDesc(
            String resourceType
    );

    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(
            String resourceType,
            String resourceId
    );

    List<AuditLog> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime start,
            LocalDateTime end
    );
}