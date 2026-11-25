package com.hospital.ehr.radiology.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AmendRadiologyReportRequest {

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

    public AmendRadiologyReportRequest() {
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
}