package com.hospital.ehr.radiology.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.radiology.dto.AmendRadiologyReportRequest;
import com.hospital.ehr.radiology.dto.CreateRadiologyReportRequest;
import com.hospital.ehr.radiology.dto.RadiologyReportResponse;
import com.hospital.ehr.radiology.service.RadiologyReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/radiology/reports")
@SecurityRequirement(name = "bearerAuth")
public class RadiologyReportController {

    private final RadiologyReportService radiologyReportService;
    private final UserRepository userRepository;

    public RadiologyReportController(
            RadiologyReportService radiologyReportService,
            UserRepository userRepository
    ) {
        this.radiologyReportService = radiologyReportService;
        this.userRepository = userRepository;
    }

    @PostMapping
        @PreAuthorize("hasRole('RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<RadiologyReportResponse> createReport(
            @Valid @RequestBody CreateRadiologyReportRequest request,
            Authentication authentication
    ) {

        User reportingDoctor =
                getAuthenticatedUser(authentication);

        RadiologyReportResponse response =
                radiologyReportService.createReport(
                        request,
                        reportingDoctor.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{reportId}/finalize")
        @PreAuthorize("hasRole('RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<RadiologyReportResponse> finalizeReport(
            @PathVariable Long reportId,
            Authentication authentication
    ) {

        User reportingDoctor =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyReportService.finalizeReport(
                        reportId,
                        reportingDoctor.getId()
                )
        );
    }

    @PutMapping("/{reportId}/amend")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RadiologyReportResponse> amendReport(
            @PathVariable Long reportId,
            @Valid @RequestBody AmendRadiologyReportRequest request,
            Authentication authentication
    ) {

        User reportingDoctor =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyReportService.amendReport(
                        reportId,
                        request,
                        reportingDoctor.getId()
                )
        );
    }

        @GetMapping("/{reportId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyReportResponse> getReport(
            @PathVariable Long reportId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyReportService.getReportById(
                        reportId,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/orders/{radiologyOrderId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<List<RadiologyReportResponse>> getReportsByOrder(
            @PathVariable Long radiologyOrderId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyReportService.getReportsByOrder(
                        radiologyOrderId,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/users/{userId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<RadiologyReportResponse>> getReportsByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                radiologyReportService.getReportsByUser(userId)
        );
    }

        @GetMapping("/status/{status}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyReportResponse>> getReportsByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                radiologyReportService.getReportsByStatus(status)
        );
    }

        @GetMapping("/critical")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyReportResponse>> getCriticalReports() {

        return ResponseEntity.ok(
                radiologyReportService.getCriticalReports()
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