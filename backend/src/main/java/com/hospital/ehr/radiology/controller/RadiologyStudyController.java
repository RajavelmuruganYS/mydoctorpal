package com.hospital.ehr.radiology.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.radiology.dto.CreateRadiologyStudyRequest;
import com.hospital.ehr.radiology.dto.RadiologyStudyResponse;
import com.hospital.ehr.radiology.service.RadiologyStudyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/radiology/studies")
@SecurityRequirement(name = "bearerAuth")
public class RadiologyStudyController {

    private final RadiologyStudyService radiologyStudyService;
    private final UserRepository userRepository;

    public RadiologyStudyController(
            RadiologyStudyService radiologyStudyService,
            UserRepository userRepository
    ) {
        this.radiologyStudyService = radiologyStudyService;
        this.userRepository = userRepository;
    }

        @PostMapping
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<RadiologyStudyResponse> createStudy(
            @Valid @RequestBody CreateRadiologyStudyRequest request
    ) {

        RadiologyStudyResponse response =
                radiologyStudyService.createStudy(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

        @GetMapping("/{id}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyStudyResponse> getStudy(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyStudyService.getStudyById(
                        id,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/orders/{radiologyOrderId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<List<RadiologyStudyResponse>> getStudiesByOrder(
            @PathVariable Long radiologyOrderId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyStudyService.getStudiesByOrder(
                        radiologyOrderId,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/orders/{radiologyOrderId}/latest")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyStudyResponse> getLatestStudyForOrder(
            @PathVariable Long radiologyOrderId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyStudyService.getLatestStudyForOrder(
                        radiologyOrderId,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/accession/{accessionNumber}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyStudyResponse> getStudyByAccessionNumber(
            @PathVariable String accessionNumber,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyStudyService.getStudyByAccessionNumber(
                        accessionNumber,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/uid/{studyInstanceUid}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyStudyResponse> getStudyByInstanceUid(
            @PathVariable String studyInstanceUid,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyStudyService.getStudyByInstanceUid(
                        studyInstanceUid,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/status/{status}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyStudyResponse>> getStudiesByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                radiologyStudyService.getStudiesByStatus(status)
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );
    }
}