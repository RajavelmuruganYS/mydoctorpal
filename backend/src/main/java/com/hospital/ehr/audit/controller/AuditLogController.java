package com.hospital.ehr.audit.controller;

import com.hospital.ehr.audit.dto.AuditLogResponse;
import com.hospital.ehr.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                auditLogService.getById(id)
        );
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                auditLogService.getByUser(userId)
        );
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<List<AuditLogResponse>> getByPatient(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(
                auditLogService.getByPatient(patientId)
        );
    }

    @GetMapping("/actions/{action}")
    public ResponseEntity<List<AuditLogResponse>> getByAction(
            @PathVariable String action
    ) {
        return ResponseEntity.ok(
                auditLogService.getByAction(action)
        );
    }

    @GetMapping("/resources/{resourceType}/{resourceId}")
    public ResponseEntity<List<AuditLogResponse>> getByResource(
            @PathVariable String resourceType,
            @PathVariable String resourceId
    ) {
        return ResponseEntity.ok(
                auditLogService.getByResource(
                        resourceType,
                        resourceId
                )
        );
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogResponse>> getByDateRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        return ResponseEntity.ok(
                auditLogService.getByDateRange(start, end)
        );
    }
}