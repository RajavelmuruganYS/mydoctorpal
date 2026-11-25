package com.hospital.ehr.inpatient.entity;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "admissions")
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admitting_doctor_id", nullable = false)
    private User admittingDoctor;

    @Column(name = "bed_number", length = 50)
    private String bedNumber;

    @Column(name = "admission_date", nullable = false)
    private LocalDateTime admissionDate;

    @Column(name = "discharge_date")
    private LocalDateTime dischargeDate;

    @Column(name = "admission_category", nullable = false, length = 50)
    private String admissionCategory;

    @Column(name = "diagnosis_on_admission", columnDefinition = "TEXT")
    private String diagnosisOnAdmission;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (admissionDate == null) {
            admissionDate = now;
        }

        if (admissionCategory == null || admissionCategory.isBlank()) {
            admissionCategory = "GENERAL";
        }

        if (status == null || status.isBlank()) {
            status = "ADMITTED";
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Admission() {
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

    public User getAdmittingDoctor() {
        return admittingDoctor;
    }

    public void setAdmittingDoctor(User admittingDoctor) {
        this.admittingDoctor = admittingDoctor;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public LocalDateTime getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(LocalDateTime admissionDate) {
        this.admissionDate = admissionDate;
    }

    public LocalDateTime getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(LocalDateTime dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public String getAdmissionCategory() {
        return admissionCategory;
    }

    public void setAdmissionCategory(String admissionCategory) {
        this.admissionCategory = admissionCategory;
    }

    public String getDiagnosisOnAdmission() {
        return diagnosisOnAdmission;
    }

    public void setDiagnosisOnAdmission(String diagnosisOnAdmission) {
        this.diagnosisOnAdmission = diagnosisOnAdmission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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