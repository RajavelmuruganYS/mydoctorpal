package com.hospital.ehr.inpatient.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAdmissionRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @Size(max = 50, message = "Bed number must not exceed 50 characters")
    private String bedNumber;

    @Size(max = 50, message = "Admission category must not exceed 50 characters")
    private String admissionCategory;

    @Size(max = 10000, message = "Diagnosis must not exceed 10000 characters")
    private String diagnosisOnAdmission;

    public CreateAdmissionRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getAdmissionCategory() {
        return admissionCategory;
    }

    public void setAdmissionCategory(String admissionCategory) {
        this.admissionCategory = admissionCategory;
    }

    public String getDiagnosisOnAdmission() {
        return diagnosisOnAdmission;
    }

    public void setDiagnosisOnAdmission(String diagnosisOnAdmission) {
        this.diagnosisOnAdmission = diagnosisOnAdmission;
    }
}