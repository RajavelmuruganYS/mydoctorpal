package com.hospital.ehr.prescription.entity;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "prescriptions",
        indexes = {
                @Index(
                        name = "idx_prescriptions_patient_id",
                        columnList = "patient_id"
                ),
                @Index(
                        name = "idx_prescriptions_doctor_id",
                        columnList = "doctor_id"
                ),
                @Index(
                        name = "idx_prescriptions_date",
                        columnList = "prescription_date"
                ),
                @Index(
                        name = "idx_prescriptions_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_prescriptions_dispense_status",
                        columnList = "dispense_status"
                )
        }
)
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_prescriptions_patient"
            )
    )
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_prescriptions_doctor"
            )
    )
    private User doctor;

    @Column(
            name = "diagnosis_code",
            nullable = false,
            length = 20
    )
    private String diagnosisCode;

    @Column(
            name = "prescription_date",
            nullable = false
    )
    private LocalDate prescriptionDate;

    @Column(
            name = "doctor_signature_hash",
            nullable = false,
            length = 500
    )
    private String doctorSignatureHash;

    @Column(
            name = "dispense_status",
            nullable = false,
            length = 30
    )
    private String dispenseStatus;

    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDate expiresAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Prescription() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (prescriptionDate == null) {
            prescriptionDate = LocalDate.now();
        }

        if (expiresAt == null) {
            expiresAt = prescriptionDate.plusDays(30);
        }

        if (dispenseStatus == null || dispenseStatus.isBlank()) {
            dispenseStatus = "NOT_DISPENSED";
        }

        if (status == null || status.isBlank()) {
            status = "DRAFT";
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
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

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getDoctorSignatureHash() {
        return doctorSignatureHash;
    }

    public void setDoctorSignatureHash(String doctorSignatureHash) {
        this.doctorSignatureHash = doctorSignatureHash;
    }

    public String getDispenseStatus() {
        return dispenseStatus;
    }

    public void setDispenseStatus(String dispenseStatus) {
        this.dispenseStatus = dispenseStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDate expiresAt) {
        this.expiresAt = expiresAt;
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