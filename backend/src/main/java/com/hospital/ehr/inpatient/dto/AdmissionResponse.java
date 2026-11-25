package com.hospital.ehr.inpatient.dto;

import com.hospital.ehr.inpatient.entity.Admission;

import java.time.LocalDateTime;

public class AdmissionResponse {

    private Long id;
    private Long patientId;
    private Long admittingDoctorId;

    private String bedNumber;

    private LocalDateTime admissionDate;
    private LocalDateTime dischargeDate;

    private String admissionCategory;
    private String diagnosisOnAdmission;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdmissionResponse() {
    }

    public AdmissionResponse(Admission admission) {

        this.id = admission.getId();

        if (admission.getPatient() != null) {
            this.patientId = admission.getPatient().getId();
        }

        if (admission.getAdmittingDoctor() != null) {
            this.admittingDoctorId = admission.getAdmittingDoctor().getId();
        }

        this.bedNumber = admission.getBedNumber();
        this.admissionDate = admission.getAdmissionDate();
        this.dischargeDate = admission.getDischargeDate();
        this.admissionCategory = admission.getAdmissionCategory();
        this.diagnosisOnAdmission = admission.getDiagnosisOnAdmission();
        this.status = admission.getStatus();
        this.createdAt = admission.getCreatedAt();
        this.updatedAt = admission.getUpdatedAt();
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

    public Long getAdmittingDoctorId() {
        return admittingDoctorId;
    }

    public void setAdmittingDoctorId(Long admittingDoctorId) {
        this.admittingDoctorId = admittingDoctorId;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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