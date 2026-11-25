package com.hospital.ehr.audit.service;

import com.hospital.ehr.audit.dto.AuditLogResponse;
import com.hospital.ehr.audit.entity.AuditLog;
import com.hospital.ehr.audit.repository.AuditLogRepository;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            UserRepository userRepository,
            PatientRepository patientRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    public AuditLogResponse log(
            Long userId,
            Long patientId,
            String action,
            String resourceType,
            String resourceId,
            String description,
            String ipAddress,
            String userAgent
    ) {

        AuditLog auditLog = new AuditLog();

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            new RuntimeException("User not found"));

            auditLog.setUser(user);
        }

        if (patientId != null) {
            Patient patient = patientRepository.findById(patientId)
                    .orElseThrow(() ->
                            new RuntimeException("Patient not found"));

            auditLog.setPatient(patient);
        }

        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);

        AuditLog savedAuditLog =
                auditLogRepository.save(auditLog);

        return AuditLogResponse.fromEntity(savedAuditLog);
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getById(Long id) {

        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Audit log not found"));

        return AuditLogResponse.fromEntity(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return auditLogRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByPatient(Long patientId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found"));

        return auditLogRepository
                .findByPatientOrderByCreatedAtDesc(patient)
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByAction(String action) {

        return auditLogRepository
                .findByActionOrderByCreatedAtDesc(
                        action.trim().toUpperCase()
                )
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByResource(
            String resourceType,
            String resourceId
    ) {

        return auditLogRepository
                .findByResourceTypeAndResourceIdOrderByCreatedAtDesc(
                        resourceType.trim().toUpperCase(),
                        resourceId
                )
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getByDateRange(
            LocalDateTime start,
            LocalDateTime end
    ) {

        if (start == null || end == null) {
            throw new RuntimeException(
                    "Start and end dates are required"
            );
        }

        if (start.isAfter(end)) {
            throw new RuntimeException(
                    "Start date cannot be after end date"
            );
        }

        return auditLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(
                        start,
                        end
                )
                .stream()
                .map(AuditLogResponse::fromEntity)
                .toList();
    }
}