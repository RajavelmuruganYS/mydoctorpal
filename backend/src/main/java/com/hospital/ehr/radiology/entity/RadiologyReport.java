package com.hospital.ehr.radiology.entity;

import com.hospital.ehr.auth.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "radiology_reports")
public class RadiologyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "radiology_order_id", nullable = false)
    private RadiologyOrder radiologyOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    private User reportedByUser;

    @Column(name = "report_text", nullable = false, columnDefinition = "TEXT")
    private String reportText;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(columnDefinition = "TEXT")
    private String impression;

    @Column(name = "critical_finding", nullable = false)
    private Boolean criticalFinding;

    @Column(name = "critical_finding_notes", columnDefinition = "TEXT")
    private String criticalFindingNotes;

    @Column(name = "compared_with_prior_study", nullable = false)
    private Boolean comparedWithPriorStudy;

    @Column(name = "prior_study_reference")
    private String priorStudyReference;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "signed_at")
    private LocalDateTime signedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RadiologyReport() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (criticalFinding == null) {
            criticalFinding = false;
        }

        if (comparedWithPriorStudy == null) {
            comparedWithPriorStudy = false;
        }

        if (status == null || status.isBlank()) {
            status = "DRAFT";
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

    public RadiologyOrder getRadiologyOrder() {
        return radiologyOrder;
    }

    public void setRadiologyOrder(RadiologyOrder radiologyOrder) {
        this.radiologyOrder = radiologyOrder;
    }

    public User getReportedByUser() {
        return reportedByUser;
    }

    public void setReportedByUser(User reportedByUser) {
        this.reportedByUser = reportedByUser;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public String getFindings() {
        return findings;
    }

    public void setFindings(String findings) {
        this.findings = findings;
    }

    public String getImpression() {
        return impression;
    }

    public void setImpression(String impression) {
        this.impression = impression;
    }

    public Boolean getCriticalFinding() {
        return criticalFinding;
    }

    public void setCriticalFinding(Boolean criticalFinding) {
        this.criticalFinding = criticalFinding;
    }

    public String getCriticalFindingNotes() {
        return criticalFindingNotes;
    }

    public void setCriticalFindingNotes(String criticalFindingNotes) {
        this.criticalFindingNotes = criticalFindingNotes;
    }

    public Boolean getComparedWithPriorStudy() {
        return comparedWithPriorStudy;
    }

    public void setComparedWithPriorStudy(Boolean comparedWithPriorStudy) {
        this.comparedWithPriorStudy = comparedWithPriorStudy;
    }

    public String getPriorStudyReference() {
        return priorStudyReference;
    }

    public void setPriorStudyReference(String priorStudyReference) {
        this.priorStudyReference = priorStudyReference;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSignedAt() {
        return signedAt;
    }

    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
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