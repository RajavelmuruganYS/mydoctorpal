package com.hospital.ehr.radiology.dto;

import com.hospital.ehr.radiology.entity.RadiologyReport;

import java.time.LocalDateTime;

public class RadiologyReportResponse {

    private Long id;
    private Long radiologyOrderId;
    private Long reportedByUserId;

    private String reportText;
    private String findings;
    private String impression;

    private Boolean criticalFinding;
    private String criticalFindingNotes;

    private Boolean comparedWithPriorStudy;
    private String priorStudyReference;

    private String status;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RadiologyReportResponse() {
    }

    public static RadiologyReportResponse fromEntity(RadiologyReport report) {
        RadiologyReportResponse response = new RadiologyReportResponse();

        response.setId(report.getId());
        response.setRadiologyOrderId(report.getRadiologyOrder().getId());
        response.setReportedByUserId(report.getReportedByUser().getId());

        response.setReportText(report.getReportText());
        response.setFindings(report.getFindings());
        response.setImpression(report.getImpression());

        response.setCriticalFinding(report.getCriticalFinding());
        response.setCriticalFindingNotes(report.getCriticalFindingNotes());

        response.setComparedWithPriorStudy(
                report.getComparedWithPriorStudy()
        );
        response.setPriorStudyReference(
                report.getPriorStudyReference()
        );

        response.setStatus(report.getStatus());
        response.setSignedAt(report.getSignedAt());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRadiologyOrderId() {
        return radiologyOrderId;
    }

    public void setRadiologyOrderId(Long radiologyOrderId) {
        this.radiologyOrderId = radiologyOrderId;
    }

    public Long getReportedByUserId() {
        return reportedByUserId;
    }

    public void setReportedByUserId(Long reportedByUserId) {
        this.reportedByUserId = reportedByUserId;
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