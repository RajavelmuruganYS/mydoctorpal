package com.hospital.ehr.radiology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRadiologyOrderRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Modality is required")
    @Size(max = 30, message = "Modality must not exceed 30 characters")
    private String modality;

    @NotBlank(message = "Clinical indication is required")
    @Size(max = 10000, message = "Clinical indication must not exceed 10000 characters")
    private String clinicalIndication;

    @Size(max = 10000, message = "Clinical history must not exceed 10000 characters")
    private String clinicalHistory;

    @Size(max = 30, message = "Urgency must not exceed 30 characters")
    private String urgency;

    public CreateRadiologyOrderRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getClinicalIndication() {
        return clinicalIndication;
    }

    public void setClinicalIndication(String clinicalIndication) {
        this.clinicalIndication = clinicalIndication;
    }

    public String getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(String clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
}