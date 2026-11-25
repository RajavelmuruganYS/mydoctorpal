package com.hospital.ehr.pharmacy.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.pharmacy.dto.DispensationResponse;
import com.hospital.ehr.pharmacy.dto.DispensePrescriptionRequest;
import com.hospital.ehr.pharmacy.entity.PrescriptionDispensation;
import com.hospital.ehr.pharmacy.service.PharmacyService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pharmacy")
@SecurityRequirement(name = "bearerAuth")
public class PharmacyController {

    private final PharmacyService pharmacyService;
    private final UserRepository userRepository;

    public PharmacyController(
            PharmacyService pharmacyService,
            UserRepository userRepository) {

        this.pharmacyService = pharmacyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/prescriptions/{prescriptionId}/dispense")
    @PreAuthorize("hasRole('PHARMACIST')")
    public ResponseEntity<?> dispensePrescription(
            @PathVariable Long prescriptionId,
            @Valid @RequestBody DispensePrescriptionRequest request,
            Authentication authentication) {

        User pharmacist =
                getAuthenticatedUser(authentication);

        PrescriptionDispensation dispensation =
                pharmacyService.dispensePrescription(
                        prescriptionId,
                        pharmacist.getId(),
                        request.getNotes()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Prescription dispensed successfully.",
                        "dispensation",
                        new DispensationResponse(dispensation)
                )
        );
    }

    @GetMapping("/dispensations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','DOCTOR')")
    public ResponseEntity<?> getDispensationById(
            @PathVariable Long id,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        PrescriptionDispensation dispensation =
                pharmacyService.getDispensationById(
                        id,
                        authenticatedUser
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "dispensation",
                        new DispensationResponse(dispensation)
                )
        );
    }

    @GetMapping("/prescriptions/{prescriptionId}/dispensations")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST','DOCTOR')")
    public ResponseEntity<?> getPrescriptionDispensations(
            @PathVariable Long prescriptionId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<PrescriptionDispensation> dispensations =
                pharmacyService.getPrescriptionDispensations(
                        prescriptionId,
                        authenticatedUser
                );

        List<DispensationResponse> response =
                dispensations.stream()
                        .map(DispensationResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "dispensations", response
                )
        );
    }

    @GetMapping("/pharmacists/{pharmacistId}/dispensations")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<?> getPharmacistDispensations(
            @PathVariable Long pharmacistId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<PrescriptionDispensation> dispensations =
                pharmacyService.getPharmacistDispensations(
                        pharmacistId,
                        authenticatedUser
                );

        List<DispensationResponse> response =
                dispensations.stream()
                        .map(DispensationResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "dispensations", response
                )
        );
    }

    @GetMapping("/dispensations/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<?> getDispensationsByStatus(
            @PathVariable String status) {

        List<PrescriptionDispensation> dispensations =
                pharmacyService.getDispensationsByStatus(
                        status.toUpperCase()
                );

        List<DispensationResponse> response =
                dispensations.stream()
                        .map(DispensationResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "dispensations", response
                )
        );
    }

    private User getAuthenticatedUser(
            Authentication authentication) {

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