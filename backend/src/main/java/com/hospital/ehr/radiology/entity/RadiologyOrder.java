package com.hospital.ehr.radiology.entity;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "radiology_orders")
public class RadiologyOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordering_doctor_id", nullable = false)
    private User orderingDoctor;

    @Column(nullable = false, length = 30)
    private String modality;

    @Column(name = "clinical_indication", nullable = false, columnDefinition = "TEXT")
    private String clinicalIndication;

    @Column(name = "clinical_history", columnDefinition = "TEXT")
    private String clinicalHistory;

    @Column(nullable = false, length = 30)
    private String urgency;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "study_started_at")
    private LocalDateTime studyStartedAt;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "radiation_dose", precision = 10, scale = 4)
    private BigDecimal radiationDose;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (urgency == null || urgency.isBlank()) {
            urgency = "ROUTINE";
        }

        if (status == null || status.isBlank()) {
            status = "ORDERED";
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public RadiologyOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getOrderingDoctor() {
        return orderingDoctor;
    }

    public void setOrderingDoctor(User orderingDoctor) {
        this.orderingDoctor = orderingDoctor;
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