package com.hospital.ehr.radiology.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "radiology_studies")
public class RadiologyStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "radiology_order_id", nullable = false)
    private RadiologyOrder radiologyOrder;

    @Column(name = "accession_number", nullable = false, unique = true, length = 100)
    private String accessionNumber;

    @Column(name = "study_instance_uid", unique = true, length = 255)
    private String studyInstanceUid;

    @Column(name = "series_count")
    private Integer seriesCount;

    @Column(name = "image_count")
    private Integer imageCount;

    @Column(name = "storage_type", nullable = false, length = 30)
    private String storageType;

    @Column(name = "storage_reference", length = 1000)
    private String storageReference;

    @Column(name = "viewer_url", length = 2000)
    private String viewerUrl;

    @Column(name = "study_status", nullable = false, length = 30)
    private String studyStatus;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public RadiologyStudy() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (storageType == null || storageType.isBlank()) {
            storageType = "PACS";
        }

        if (studyStatus == null || studyStatus.isBlank()) {
            studyStatus = "AVAILABLE";
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