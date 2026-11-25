package com.hospital.ehr.prescription.dto;

import com.hospital.ehr.prescription.entity.Drug;

import java.time.LocalDateTime;

public class DrugResponse {

    private Long id;
    private String brandName;
    private String genericName;
    private String dosageForm;
    private String strength;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DrugResponse() {
    }

    public DrugResponse(Drug drug) {
        this.id = drug.getId();
        this.brandName = drug.getBrandName();
        this.genericName = drug.getGenericName();
        this.dosageForm = drug.getDosageForm();
        this.strength = drug.getStrength();
        this.active = drug.getActive();
        this.createdAt = drug.getCreatedAt();
        this.updatedAt = drug.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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