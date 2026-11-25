package com.hospital.ehr.radiology.dto;

import com.hospital.ehr.radiology.entity.RadiologyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RadiologyOrderResponse {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long orderingDoctorId;
    private String orderingDoctorName;
    private String modality;
    private String clinicalIndication;
    private String clinicalHistory;
    private String urgency;
    private LocalDateTime scheduledAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime studyStartedAt;
    private String status;
    private BigDecimal radiationDose;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RadiologyOrderResponse() {
    }

    public static RadiologyOrderResponse fromEntity(RadiologyOrder order) {
        RadiologyOrderResponse response = new RadiologyOrderResponse();

        response.setId(order.getId());
        response.setPatientId(order.getPatient().getId());
        response.setPatientName(formatPersonName(order.getPatient() == null ? null : order.getPatient().getUser()));
        response.setOrderingDoctorId(order.getOrderingDoctor().getId());
        response.setOrderingDoctorName(formatPersonName(order.getOrderingDoctor()));
        response.setModality(order.getModality());
        response.setClinicalIndication(order.getClinicalIndication());
        response.setClinicalHistory(order.getClinicalHistory());
        response.setUrgency(order.getUrgency());
        response.setScheduledAt(order.getScheduledAt());
        response.setArrivedAt(order.getArrivedAt());
        response.setStudyStartedAt(order.getStudyStartedAt());
        response.setStatus(order.getStatus());
        response.setRadiationDose(order.getRadiationDose());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        return response;
    }

    private static String formatPersonName(com.hospital.ehr.auth.entity.User user) {
        if (user == null) {
            return null;
        }

        String first = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String last = user.getLastName() == null ? "" : user.getLastName().trim();

        return String.join(" ", java.util.stream.Stream.of(first, last)
                .filter(value -> !value.isBlank())
                .toList());
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getOrderingDoctorId() {
        return orderingDoctorId;
    }

    public void setOrderingDoctorId(Long orderingDoctorId) {
        this.orderingDoctorId = orderingDoctorId;
    }

    public String getOrderingDoctorName() {
        return orderingDoctorName;
    }

    public void setOrderingDoctorName(String orderingDoctorName) {
        this.orderingDoctorName = orderingDoctorName;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getClinicalIndication() {
        return clinicalIndication;
    }

    public void setClinicalIndication(String clinicalIndication) {
        this.clinicalIndication = clinicalIndication;
    }

    public String getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(String clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getStudyStartedAt() {
        return studyStartedAt;
    }

    public void setStudyStartedAt(LocalDateTime studyStartedAt) {
        this.studyStartedAt = studyStartedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRadiationDose() {
        return radiationDose;
    }

    public void setRadiationDose(BigDecimal radiationDose) {
        this.radiationDose = radiationDose;
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