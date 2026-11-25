package com.hospital.ehr.radiology.dto;

import com.hospital.ehr.radiology.entity.RadiologyStudy;

import java.time.LocalDateTime;

public class RadiologyStudyResponse {

    private Long id;
    private Long radiologyOrderId;

    private String accessionNumber;
    private String studyInstanceUid;

    private Integer seriesCount;
    private Integer imageCount;

    private String storageType;
    private String storageReference;
    private String viewerUrl;

    private String studyStatus;

    private LocalDateTime performedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RadiologyStudyResponse() {
    }

    public static RadiologyStudyResponse fromEntity(RadiologyStudy study) {
        RadiologyStudyResponse response = new RadiologyStudyResponse();

        response.setId(study.getId());
        response.setRadiologyOrderId(study.getRadiologyOrder().getId());

        response.setAccessionNumber(study.getAccessionNumber());
        response.setStudyInstanceUid(study.getStudyInstanceUid());

        response.setSeriesCount(study.getSeriesCount());
        response.setImageCount(study.getImageCount());

        response.setStorageType(study.getStorageType());
        response.setStorageReference(study.getStorageReference());
        response.setViewerUrl(study.getViewerUrl());

        response.setStudyStatus(study.getStudyStatus());

        response.setPerformedAt(study.getPerformedAt());
        response.setCreatedAt(study.getCreatedAt());
        response.setUpdatedAt(study.getUpdatedAt());

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

    public String getStudyStatus() {
        return studyStatus;
    }

    public void setStudyStatus(String studyStatus) {
        this.studyStatus = studyStatus;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
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