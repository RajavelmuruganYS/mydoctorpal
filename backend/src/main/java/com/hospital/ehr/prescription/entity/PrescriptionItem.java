package com.hospital.ehr.prescription.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "prescription_items",
        indexes = {
                @Index(
                        name = "idx_prescription_items_prescription_id",
                        columnList = "prescription_id"
                ),
                @Index(
                        name = "idx_prescription_items_drug_id",
                        columnList = "drug_id"
                )
        }
)
public class PrescriptionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "prescription_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_prescription_items_prescription"
            )
    )
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "drug_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_prescription_items_drug"
            )
    )
    private Drug drug;

    @Column(
            name = "dosage_form",
            nullable = false,
            length = 100
    )
    private String dosageForm;

    @Column(
            name = "strength",
            nullable = false,
            length = 100
    )
    private String strength;

    @Column(
            name = "frequency",
            nullable = false,
            length = 100
    )
    private String frequency;

    @Column(
            name = "duration_days",
            nullable = false
    )
    private Integer durationDays;

    @Column(
            name = "special_instructions",
            columnDefinition = "TEXT"
    )
    private String specialInstructions;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public PrescriptionItem() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public Drug getDrug() {
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}