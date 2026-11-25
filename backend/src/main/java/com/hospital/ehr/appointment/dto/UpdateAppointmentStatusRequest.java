package com.hospital.ehr.appointment.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateAppointmentStatusRequest {

    @NotBlank(message = "Status is required.")
    private String status;

    public UpdateAppointmentStatusRequest() {
    }

    // =========================
    // Getters and Setters
    // =========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}