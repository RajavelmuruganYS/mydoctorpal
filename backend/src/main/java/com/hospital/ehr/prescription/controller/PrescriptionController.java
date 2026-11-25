package com.hospital.ehr.prescription.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.prescription.dto.CreatePrescriptionRequest;
import com.hospital.ehr.prescription.dto.PrescriptionItemResponse;
import com.hospital.ehr.prescription.dto.PrescriptionResponse;
import com.hospital.ehr.prescription.entity.Prescription;
import com.hospital.ehr.prescription.service.PrescriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/prescriptions")
@SecurityRequirement(name = "bearerAuth")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final UserRepository userRepository;

    public PrescriptionController(
            PrescriptionService prescriptionService,
            UserRepository userRepository
    ) {
        this.prescriptionService = prescriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createPrescription(
            @Valid @RequestBody CreatePrescriptionRequest request,
            Authentication authentication
    ) {

        User doctor = getAuthenticatedUser(authentication);

        Prescription prescription =
                prescriptionService.createPrescription(
                        request.getPatientId(),
                        doctor.getId(),
                        request.getDiagnosisCode(),
                        request.getItems()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Prescription created successfully.",
                        "prescription",
                        new PrescriptionResponse(prescription)
                )
        );
    }

    @PostMapping("/{id}/sign")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> signPrescription(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User doctor = getAuthenticatedUser(authentication);

        Prescription prescription =
                prescriptionService.signPrescription(
                        id,
                        doctor.getId()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Prescription signed successfully.",
                        "prescription",
                        new PrescriptionResponse(prescription)
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','PHARMACIST','PATIENT')"
    )
    public ResponseEntity<?> getPrescriptionById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        Prescription prescription =
                prescriptionService.getPrescriptionById(
                        id,
                        authenticatedUser
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "prescription",
                        new PrescriptionResponse(prescription)
                )
        );
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','PHARMACIST','PATIENT')"
    )
    public ResponseEntity<?> getPatientPrescriptions(
            @PathVariable Long patientId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<PrescriptionResponse> prescriptions =
                prescriptionService
                        .getPatientPrescriptions(
                                patientId,
                                authenticatedUser
                        )
                        .stream()
                        .map(PrescriptionResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "prescriptions", prescriptions
                )
        );
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public ResponseEntity<?> getDoctorPrescriptions(
            @PathVariable Long doctorId
    ) {

        List<PrescriptionResponse> prescriptions =
                prescriptionService
                        .getDoctorPrescriptions(doctorId)
                        .stream()
                        .map(PrescriptionResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "prescriptions", prescriptions
                )
        );
    }

    @GetMapping("/{id}/items")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','PHARMACIST','PATIENT')"
    )
    public ResponseEntity<?> getPrescriptionItems(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<PrescriptionItemResponse> items =
                prescriptionService
                        .getPrescriptionItems(
                                id,
                                authenticatedUser
                        )
                        .stream()
                        .map(PrescriptionItemResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "items", items
                )
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        if (authentication == null
                || authentication.getName() == null) {

            throw new SecurityException(
                    "Authenticated user not found."
            );
        }

        return userRepository.findByEmail(
                        authentication.getName()
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found."
                        )
                );
    }
}