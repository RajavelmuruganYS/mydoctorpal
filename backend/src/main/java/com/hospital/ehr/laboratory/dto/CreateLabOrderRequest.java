package com.hospital.ehr.laboratory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateLabOrderRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotBlank(message = "Test name is required")
    @Size(
            max = 200,
            message = "Test name must not exceed 200 characters"
    )
    private String testName;

    @Size(
            max = 50,
            message = "Test code must not exceed 50 characters"
    )
    private String testCode;

    @Size(
            max = 30,
            message = "Priority must not exceed 30 characters"
    )
    private String priority;

    @Size(
            max = 5000,
            message = "Clinical notes must not exceed 5000 characters"
    )
    private String clinicalNotes;

    public CreateLabOrderRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }
}