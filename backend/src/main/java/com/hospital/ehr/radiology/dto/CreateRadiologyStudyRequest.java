package com.hospital.ehr.radiology.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRadiologyStudyRequest {

    @NotNull(message = "Radiology order ID is required")
    private Long radiologyOrderId;

    @NotBlank(message = "Accession number is required")
    @Size(max = 100, message = "Accession number must not exceed 100 characters")
    private String accessionNumber;

    @Size(max = 255, message = "Study Instance UID must not exceed 255 characters")
    private String studyInstanceUid;

    @Min(value = 0, message = "Series count cannot be negative")
    private Integer seriesCount;

    @Min(value = 0, message = "Image count cannot be negative")
    private Integer imageCount;

    @Size(max = 30, message = "Storage type must not exceed 30 characters")
    private String storageType;

    @Size(max = 1000, message = "Storage reference must not exceed 1000 characters")
    private String storageReference;

    @Size(max = 2000, message = "Viewer URL must not exceed 2000 characters")
    private String viewerUrl;

    public CreateRadiologyStudyRequest() {
    }

    public Long getRadiologyOrderId() {
        return radiologyOrderId;
    }

    public void setRadiologyOrderId(Long radiologyOrderId) {
        this.radiologyOrderId = radiologyOrderId;
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    public String getStudyInstanceUid() {
        return studyInstanceUid;
    }

    public void setStudyInstanceUid(String studyInstanceUid) {
        this.studyInstanceUid = studyInstanceUid;
    }

    public Integer getSeriesCount() {
        return seriesCount;
    }

    public void setSeriesCount(Integer seriesCount) {
        this.seriesCount = seriesCount;
    }

    public Integer getImageCount() {
        return imageCount;
    }

    public void setImageCount(Integer imageCount) {
        this.imageCount = imageCount;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(String storageReference) {
        this.storageReference = storageReference;
    }

    public String getViewerUrl() {
        return viewerUrl;
    }

    public void setViewerUrl(String viewerUrl) {
        this.viewerUrl = viewerUrl;
    }
}