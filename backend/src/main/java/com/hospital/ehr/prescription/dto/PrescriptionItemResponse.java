package com.hospital.ehr.prescription.dto;

import com.hospital.ehr.prescription.entity.PrescriptionItem;

public class PrescriptionItemResponse {

    private Long id;
    private Long prescriptionId;
    private Long drugId;
    private String brandName;
    private String genericName;
    private String dosageForm;
    private String strength;
    private String frequency;
    private Integer durationDays;
    private String specialInstructions;

    public PrescriptionItemResponse() {
    }

    public PrescriptionItemResponse(PrescriptionItem item) {
        this.id = item.getId();

        this.prescriptionId = item.getPrescription() != null
                ? item.getPrescription().getId()
                : null;

        this.drugId = item.getDrug() != null
                ? item.getDrug().getId()
                : null;

        if (item.getDrug() != null) {
            this.brandName = item.getDrug().getBrandName();
            this.genericName = item.getDrug().getGenericName();
        }

        this.dosageForm = item.getDosageForm();
        this.strength = item.getStrength();
        this.frequency = item.getFrequency();
        this.durationDays = item.getDurationDays();
        this.specialInstructions = item.getSpecialInstructions();
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

    public Long getDrugId() {
        return drugId;
    }

    public void setDrugId(Long drugId) {
        this.drugId = drugId;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}