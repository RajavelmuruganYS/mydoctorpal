package com.hospital.ehr.vitals.dto;

import com.hospital.ehr.vitals.entity.Vital;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VitalResponse {

    private Long id;
    private Long patientId;
    private Long recordedByUserId;

    private BigDecimal temperature;
    private Integer bpSystolic;
    private Integer bpDiastolic;
    private Integer pulseRate;
    private BigDecimal spo2;
    private Integer respiratoryRate;

    private LocalDateTime recordedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VitalResponse() {
    }

    public VitalResponse(Vital vital) {

        this.id = vital.getId();

        if (vital.getPatient() != null) {
            this.patientId = vital.getPatient().getId();
        }

        if (vital.getRecordedByUser() != null) {
            this.recordedByUserId = vital.getRecordedByUser().getId();
        }

        this.temperature = vital.getTemperature();
        this.bpSystolic = vital.getBpSystolic();
        this.bpDiastolic = vital.getBpDiastolic();
        this.pulseRate = vital.getPulseRate();
        this.spo2 = vital.getSpo2();
        this.respiratoryRate = vital.getRespiratoryRate();

        this.recordedAt = vital.getRecordedAt();
        this.createdAt = vital.getCreatedAt();
        this.updatedAt = vital.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getRecordedByUserId() {
        return recordedByUserId;
    }

    public void setRecordedByUserId(Long recordedByUserId) {
        this.recordedByUserId = recordedByUserId;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Integer getBpSystolic() {
        return bpSystolic;
    }

    public void setBpSystolic(Integer bpSystolic) {
        this.bpSystolic = bpSystolic;
    }

    public Integer getBpDiastolic() {
        return bpDiastolic;
    }

    public void setBpDiastolic(Integer bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public BigDecimal getSpo2() {
        return spo2;
    }

    public void setSpo2(BigDecimal spo2) {
        this.spo2 = spo2;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}