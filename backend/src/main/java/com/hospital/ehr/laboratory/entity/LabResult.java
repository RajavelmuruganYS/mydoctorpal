package com.hospital.ehr.laboratory.entity;

import com.hospital.ehr.auth.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "lab_results",
        indexes = {
                @Index(
                        name = "idx_lab_results_lab_order_id",
                        columnList = "lab_order_id"
                ),
                @Index(
                        name = "idx_lab_results_technician_id",
                        columnList = "lab_technician_id"
                ),
                @Index(
                        name = "idx_lab_results_status",
                        columnList = "result_status"
                ),
                @Index(
                        name = "idx_lab_results_performed_at",
                        columnList = "performed_at"
                )
        }
)
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lab_order_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lab_results_order"
            )
    )
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "lab_technician_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lab_results_technician"
            )
    )
    private User labTechnician;

    @Column(name = "result_value", columnDefinition = "TEXT")
    private String resultValue;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "reference_range", length = 200)
    private String referenceRange;

    @Column(name = "interpretation", columnDefinition = "TEXT")
    private String interpretation;

    @Column(name = "result_status", nullable = false, length = 30)
    private String resultStatus;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LabResult() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (resultStatus == null || resultStatus.isBlank()) {
            resultStatus = "PRELIMINARY";
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

    public LabOrder getLabOrder() {
        return labOrder;
    }

    public void setLabOrder(LabOrder labOrder) {
        this.labOrder = labOrder;
    }

    public User getLabTechnician() {
        return labTechnician;
    }

    public void setLabTechnician(User labTechnician) {
        this.labTechnician = labTechnician;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getReferenceRange() {
        return referenceRange;
    }

    public void setReferenceRange(String referenceRange) {
        this.referenceRange = referenceRange;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
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