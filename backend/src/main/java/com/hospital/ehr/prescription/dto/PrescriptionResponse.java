package com.hospital.ehr.prescription.dto;

import com.hospital.ehr.prescription.entity.Prescription;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrescriptionResponse {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String diagnosisCode;
    private LocalDate prescriptionDate;
    private String doctorSignatureHash;
    private String dispenseStatus;
    private String status;
    private LocalDate expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrescriptionResponse() {
    }

    public PrescriptionResponse(Prescription prescription) {
        this.id = prescription.getId();
        this.patientId = prescription.getPatient() != null
                ? prescription.getPatient().getId()
                : null;
        this.doctorId = prescription.getDoctor() != null
                ? prescription.getDoctor().getId()
                : null;
        this.diagnosisCode = prescription.getDiagnosisCode();
        this.prescriptionDate = prescription.getPrescriptionDate();
        this.doctorSignatureHash = prescription.getDoctorSignatureHash();
        this.dispenseStatus = prescription.getDispenseStatus();
        this.status = prescription.getStatus();
        this.expiresAt = prescription.getExpiresAt();
        this.createdAt = prescription.getCreatedAt();
        this.updatedAt = prescription.getUpdatedAt();
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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDoctorSignatureHash() {
        return doctorSignatureHash;
    }

    public void setDoctorSignatureHash(String doctorSignatureHash) {
        this.doctorSignatureHash = doctorSignatureHash;
    }

    public String getDispenseStatus() {
        return dispenseStatus;
    }

    public void setDispenseStatus(String dispenseStatus) {
        this.dispenseStatus = dispenseStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
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