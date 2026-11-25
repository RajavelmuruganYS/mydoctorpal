package com.hospital.ehr.pharmacy.dto;

import com.hospital.ehr.pharmacy.entity.PrescriptionDispensation;

import java.time.LocalDateTime;

public class DispensationResponse {

    private Long id;
    private Long prescriptionId;
    private Long pharmacistId;
    private LocalDateTime dispensedAt;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DispensationResponse() {
    }

    public DispensationResponse(PrescriptionDispensation dispensation) {
        this.id = dispensation.getId();

        if (dispensation.getPrescription() != null) {
            this.prescriptionId = dispensation.getPrescription().getId();
        }

        if (dispensation.getPharmacist() != null) {
            this.pharmacistId = dispensation.getPharmacist().getId();
        }

        this.dispensedAt = dispensation.getDispensedAt();
        this.status = dispensation.getStatus();
        this.notes = dispensation.getNotes();
        this.createdAt = dispensation.getCreatedAt();
        this.updatedAt = dispensation.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getPharmacistId() {
        return pharmacistId;
    }

    public void setPharmacistId(Long pharmacistId) {
        this.pharmacistId = pharmacistId;
    }

    public LocalDateTime getDispensedAt() {
        return dispensedAt;
    }

    public void setDispensedAt(LocalDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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