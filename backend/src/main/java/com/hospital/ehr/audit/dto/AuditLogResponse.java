package com.hospital.ehr.audit.dto;

import com.hospital.ehr.audit.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private Long userId;
    private Long patientId;
    private String action;
    private String resourceType;
    private String resourceId;
    private String description;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;

    public AuditLogResponse() {
    }

    public static AuditLogResponse fromEntity(AuditLog auditLog) {

        AuditLogResponse response = new AuditLogResponse();

        response.setId(auditLog.getId());

        if (auditLog.getUser() != null) {
            response.setUserId(auditLog.getUser().getId());
        }

        if (auditLog.getPatient() != null) {
            response.setPatientId(auditLog.getPatient().getId());
        }

        response.setAction(auditLog.getAction());
        response.setResourceType(auditLog.getResourceType());
        response.setResourceId(auditLog.getResourceId());
        response.setDescription(auditLog.getDescription());
        response.setIpAddress(auditLog.getIpAddress());
        response.setUserAgent(auditLog.getUserAgent());
        response.setCreatedAt(auditLog.getCreatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}