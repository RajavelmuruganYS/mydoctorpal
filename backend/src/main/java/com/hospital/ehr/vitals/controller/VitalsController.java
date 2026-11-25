package com.hospital.ehr.vitals.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.vitals.dto.CreateVitalRequest;
import com.hospital.ehr.vitals.dto.VitalResponse;
import com.hospital.ehr.vitals.service.VitalsService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vitals")
@SecurityRequirement(name = "bearerAuth")
public class VitalsController {

    private final VitalsService vitalsService;
    private final UserRepository userRepository;

    public VitalsController(
            VitalsService vitalsService,
            UserRepository userRepository
    ) {
        this.vitalsService = vitalsService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE')")
    public ResponseEntity<VitalResponse> recordVitals(
            @Valid @RequestBody CreateVitalRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        User recordedByUser = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        )
                );

        VitalResponse response =
                vitalsService.recordVitals(
                        request,
                        recordedByUser.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{vitalId}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'PATIENT'
            )
            """)
    public ResponseEntity<VitalResponse> getVitalById(
            @PathVariable Long vitalId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                vitalsService.getVitalById(
                        vitalId,
                        authenticatedUser
                )
        );
    }

    @GetMapping("/patients/{patientId}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'PATIENT'
            )
            """)
    public ResponseEntity<List<VitalResponse>> getPatientVitals(
            @PathVariable Long patientId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                vitalsService.getPatientVitals(
                        patientId,
                        authenticatedUser
                )
        );
    }

    @GetMapping("/patients/{patientId}/latest")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'PATIENT'
            )
            """)
    public ResponseEntity<VitalResponse> getLatestPatientVitals(
            @PathVariable Long patientId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                vitalsService.getLatestPatientVitals(
                        patientId,
                        authenticatedUser
                )
        );
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<List<VitalResponse>> getVitalsRecordedByUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                vitalsService.getVitalsRecordedByUser(userId)
        );
    }

    /**
     * Resolve the currently authenticated application user
     * from the email stored in the JWT authentication.
     */
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