package com.hospital.ehr.pharmacy.entity;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.prescription.entity.Prescription;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "prescription_dispensations",
        indexes = {
                @Index(
                        name = "idx_dispensations_prescription_id",
                        columnList = "prescription_id"
                ),
                @Index(
                        name = "idx_dispensations_pharmacist_id",
                        columnList = "pharmacist_id"
                ),
                @Index(
                        name = "idx_dispensations_dispensed_at",
                        columnList = "dispensed_at"
                ),
                @Index(
                        name = "idx_dispensations_status",
                        columnList = "status"
                )
        }
)
public class PrescriptionDispensation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "prescription_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_dispensations_prescription"
            )
    )
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "pharmacist_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_dispensations_pharmacist"
            )
    )
    private User pharmacist;

    @Column(name = "dispensed_at", nullable = false)
    private LocalDateTime dispensedAt;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PrescriptionDispensation() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (dispensedAt == null) {
            dispensedAt = now;
        }

        if (status == null || status.isBlank()) {
            status = "DISPENSED";
        }

        createdAt = now;
        updatedAt = now;
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

    public Prescription getPrescription() {
        return prescription;
    }

    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }

    public User getPharmacist() {
        return pharmacist;
    }

    public void setPharmacist(User pharmacist) {
        this.pharmacist = pharmacist;
    }

    public LocalDateTime getDispensedAt() {
        return dispensedAt;
    }

    public void setDispensedAt(LocalDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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