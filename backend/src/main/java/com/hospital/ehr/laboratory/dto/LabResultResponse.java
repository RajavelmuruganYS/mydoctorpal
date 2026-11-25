package com.hospital.ehr.laboratory.dto;

import com.hospital.ehr.laboratory.entity.LabResult;

import java.time.LocalDateTime;

public class LabResultResponse {

    private Long id;
    private Long labOrderId;
    private Long labTechnicianId;
    private String resultValue;
    private String unit;
    private String referenceRange;
    private String interpretation;
    private String resultStatus;
    private LocalDateTime performedAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LabResultResponse() {
    }

    public LabResultResponse(LabResult labResult) {

        this.id = labResult.getId();

        if (labResult.getLabOrder() != null) {
            this.labOrderId = labResult.getLabOrder().getId();
        }

        if (labResult.getLabTechnician() != null) {
            this.labTechnicianId = labResult.getLabTechnician().getId();
        }

        this.resultValue = labResult.getResultValue();
        this.unit = labResult.getUnit();
        this.referenceRange = labResult.getReferenceRange();
        this.interpretation = labResult.getInterpretation();
        this.resultStatus = labResult.getResultStatus();
        this.performedAt = labResult.getPerformedAt();
        this.verifiedAt = labResult.getVerifiedAt();
        this.createdAt = labResult.getCreatedAt();
        this.updatedAt = labResult.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabOrderId() {
        return labOrderId;
    }

    public void setLabOrderId(Long labOrderId) {
        this.labOrderId = labOrderId;
    }

    public Long getLabTechnicianId() {
        return labTechnicianId;
    }

    public void setLabTechnicianId(Long labTechnicianId) {
        this.labTechnicianId = labTechnicianId;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
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