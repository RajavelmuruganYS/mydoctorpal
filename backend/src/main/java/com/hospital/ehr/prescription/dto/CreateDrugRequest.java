package com.hospital.ehr.prescription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateDrugRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 200, message = "Brand name must not exceed 200 characters")
    private String brandName;

    @NotBlank(message = "Generic name is required")
    @Size(max = 200, message = "Generic name must not exceed 200 characters")
    private String genericName;

    @NotBlank(message = "Dosage form is required")
    @Size(max = 100, message = "Dosage form must not exceed 100 characters")
    private String dosageForm;

    @NotBlank(message = "Strength is required")
    @Size(max = 100, message = "Strength must not exceed 100 characters")
    private String strength;

    public CreateDrugRequest() {
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
}