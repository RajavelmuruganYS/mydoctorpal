package com.hospital.ehr.pharmacy.dto;

import jakarta.validation.constraints.Size;

public class DispensePrescriptionRequest {

    @Size(
            max = 2000,
            message = "Notes must not exceed 2000 characters"
    )
    private String notes;

    public DispensePrescriptionRequest() {
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}