package com.hospital.ehr.laboratory.dto;

import com.hospital.ehr.laboratory.entity.LabOrder;

import java.time.LocalDateTime;

public class LabOrderResponse {

    private Long id;
    private Long patientId;
    private Long doctorId;
    private String testName;
    private String testCode;
    private String priority;
    private String clinicalNotes;
    private String status;
    private LocalDateTime orderedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LabOrderResponse() {
    }

    public LabOrderResponse(LabOrder labOrder) {

        this.id = labOrder.getId();

        if (labOrder.getPatient() != null) {
            this.patientId = labOrder.getPatient().getId();
        }

        if (labOrder.getDoctor() != null) {
            this.doctorId = labOrder.getDoctor().getId();
        }

        this.testName = labOrder.getTestName();
        this.testCode = labOrder.getTestCode();
        this.priority = labOrder.getPriority();
        this.clinicalNotes = labOrder.getClinicalNotes();
        this.status = labOrder.getStatus();
        this.orderedAt = labOrder.getOrderedAt();
        this.createdAt = labOrder.getCreatedAt();
        this.updatedAt = labOrder.getUpdatedAt();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
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