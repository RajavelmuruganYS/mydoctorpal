package com.hospital.ehr.radiology.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.radiology.dto.RadiologyStudyFileResponse;
import com.hospital.ehr.radiology.service.RadiologyStudyFileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/radiology/studies")
@SecurityRequirement(name = "bearerAuth")
public class RadiologyStudyFileController {

    private final RadiologyStudyFileService radiologyStudyFileService;
    private final UserRepository userRepository;

    public RadiologyStudyFileController(
            RadiologyStudyFileService radiologyStudyFileService,
            UserRepository userRepository
    ) {
        this.radiologyStudyFileService = radiologyStudyFileService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{studyId}/files")
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<RadiologyStudyFileResponse> uploadStudyFile(
            @PathVariable Long studyId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        User authenticatedUser = getAuthenticatedUser(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(radiologyStudyFileService.uploadStudyFile(studyId, file, authenticatedUser));
    }

    @GetMapping("/{studyId}/files")
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<List<RadiologyStudyFileResponse>> getStudyFiles(
            @PathVariable Long studyId,
            Authentication authentication
    ) {
        User authenticatedUser = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(radiologyStudyFileService.getStudyFiles(studyId, authenticatedUser));
    }

    @GetMapping("/{studyId}/files/{fileId}")
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyStudyFileResponse> getStudyFile(
            @PathVariable Long studyId,
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        User authenticatedUser = getAuthenticatedUser(authentication);
        return ResponseEntity.ok(radiologyStudyFileService.getStudyFile(studyId, fileId, authenticatedUser));
    }

    @GetMapping("/{studyId}/files/{fileId}/download")
        @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<Resource> downloadStudyFile(
            @PathVariable Long studyId,
            @PathVariable Long fileId,
            Authentication authentication
    ) {
        User authenticatedUser = getAuthenticatedUser(authentication);
        RadiologyStudyFileResponse file = radiologyStudyFileService.getStudyFile(studyId, fileId, authenticatedUser);
        Resource resource = radiologyStudyFileService.getFileResource(studyId, fileId, authenticatedUser);

        String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                .body(resource);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
