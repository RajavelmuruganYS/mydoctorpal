package com.hospital.ehr.radiology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRadiologyReportRequest {

    @NotNull(message = "Radiology order ID is required")
    private Long radiologyOrderId;

    @NotBlank(message = "Report text is required")
    @Size(max = 20000, message = "Report text must not exceed 20000 characters")
    private String reportText;

    @Size(max = 20000, message = "Findings must not exceed 20000 characters")
    private String findings;

    @Size(max = 20000, message = "Impression must not exceed 20000 characters")
    private String impression;

    private Boolean criticalFinding;

    @Size(max = 10000, message = "Critical finding notes must not exceed 10000 characters")
    private String criticalFindingNotes;

    private Boolean comparedWithPriorStudy;

    @Size(max = 500, message = "Prior study reference must not exceed 500 characters")
    private String priorStudyReference;

    public CreateRadiologyReportRequest() {
    }

    public Long getRadiologyOrderId() {
        return radiologyOrderId;
    }

    public void setRadiologyOrderId(Long radiologyOrderId) {
        this.radiologyOrderId = radiologyOrderId;
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
}