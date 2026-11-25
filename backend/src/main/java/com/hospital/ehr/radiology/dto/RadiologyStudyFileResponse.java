package com.hospital.ehr.radiology.dto;

import com.hospital.ehr.radiology.entity.RadiologyStudyFile;

import java.time.LocalDateTime;

public class RadiologyStudyFileResponse {

    private Long id;
    private Long studyId;
    private String originalFileName;
    private String contentType;
    private long sizeBytes;
    private Long uploadedById;
    private LocalDateTime uploadedAt;

    public RadiologyStudyFileResponse() {
    }

    public static RadiologyStudyFileResponse fromEntity(RadiologyStudyFile file) {
        RadiologyStudyFileResponse response = new RadiologyStudyFileResponse();
        response.setId(file.getId());
        response.setStudyId(file.getStudy().getId());
        response.setOriginalFileName(file.getOriginalFileName());
        response.setContentType(file.getContentType());
        response.setSizeBytes(file.getSizeBytes());
        response.setUploadedById(file.getUploadedBy().getId());
        response.setUploadedAt(file.getUploadedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudyId() {
        return studyId;
    }

    public void setStudyId(Long studyId) {
        this.studyId = studyId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public Long getUploadedById() {
        return uploadedById;
    }

    public void setUploadedById(Long uploadedById) {
        this.uploadedById = uploadedById;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
