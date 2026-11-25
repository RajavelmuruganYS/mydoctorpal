package com.hospital.ehr.vitals.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateVitalRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @DecimalMin(value = "20.0", message = "Temperature must be at least 20°C")
    @DecimalMax(value = "50.0", message = "Temperature must not exceed 50°C")
    private BigDecimal temperature;

    @DecimalMin(value = "40", message = "Systolic BP must be at least 40")
    @DecimalMax(value = "250", message = "Systolic BP must not exceed 250")
    private Integer bpSystolic;

    @DecimalMin(value = "20", message = "Diastolic BP must be at least 20")
    @DecimalMax(value = "150", message = "Diastolic BP must not exceed 150")
    private Integer bpDiastolic;

    @DecimalMin(value = "30", message = "Pulse rate must be at least 30")
    @DecimalMax(value = "250", message = "Pulse rate must not exceed 250")
    private Integer pulseRate;

    @DecimalMin(value = "50.0", message = "SpO2 must be at least 50")
    @DecimalMax(value = "100.0", message = "SpO2 must not exceed 100")
    private BigDecimal spo2;

    @DecimalMin(value = "5", message = "Respiratory rate must be at least 5")
    @DecimalMax(value = "80", message = "Respiratory rate must not exceed 80")
    private Integer respiratoryRate;

    public CreateVitalRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Integer getBpSystolic() {
        return bpSystolic;
    }

    public void setBpSystolic(Integer bpSystolic) {
        this.bpSystolic = bpSystolic;
    }

    public Integer getBpDiastolic() {
        return bpDiastolic;
    }

    public void setBpDiastolic(Integer bpDiastolic) {
        this.bpDiastolic = bpDiastolic;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public BigDecimal getSpo2() {
        return spo2;
    }

    public void setSpo2(BigDecimal spo2) {
        this.spo2 = spo2;
    }

    public Integer getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(Integer respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }
}