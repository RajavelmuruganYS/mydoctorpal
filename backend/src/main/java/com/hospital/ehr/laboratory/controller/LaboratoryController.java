package com.hospital.ehr.laboratory.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.laboratory.dto.CreateLabOrderRequest;
import com.hospital.ehr.laboratory.dto.CreateLabResultRequest;
import com.hospital.ehr.laboratory.dto.LabOrderResponse;
import com.hospital.ehr.laboratory.dto.LabResultResponse;
import com.hospital.ehr.laboratory.entity.LabOrder;
import com.hospital.ehr.laboratory.entity.LabResult;
import com.hospital.ehr.laboratory.service.LaboratoryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/laboratory")
@SecurityRequirement(name = "bearerAuth")
public class LaboratoryController {

    private final LaboratoryService laboratoryService;
    private final UserRepository userRepository;

    public LaboratoryController(
            LaboratoryService laboratoryService,
            UserRepository userRepository) {

        this.laboratoryService = laboratoryService;
        this.userRepository = userRepository;
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<?> createLabOrder(
            @Valid @RequestBody CreateLabOrderRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User doctor = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated doctor not found."
                        ));

        LabOrder labOrder =
                laboratoryService.createLabOrder(
                        request,
                        doctor.getId()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Lab order created successfully.",
                        "labOrder", new LabOrderResponse(labOrder)
                )
        );
    }

    @PostMapping("/orders/{labOrderId}/results")
    @PreAuthorize("hasRole('LAB_TECHNICIAN')")
    public ResponseEntity<?> createLabResult(
            @PathVariable Long labOrderId,
            @Valid @RequestBody CreateLabResultRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        User technician = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated lab technician not found."
                        ));

        LabResult labResult =
                laboratoryService.createLabResult(
                        labOrderId,
                        request,
                        technician.getId()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Lab result entered successfully.",
                        "labResult", new LabResultResponse(labResult)
                )
        );
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','LAB_TECHNICIAN','PATIENT')"
    )
    public ResponseEntity<?> getLabOrderById(
            @PathVariable Long id,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        LabOrder labOrder =
                laboratoryService.getLabOrderById(
                        id,
                        authenticatedUser
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labOrder", new LabOrderResponse(labOrder)
                )
        );
    }

    @GetMapping("/results/{id}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','LAB_TECHNICIAN','PATIENT')"
    )
    public ResponseEntity<?> getLabResultById(
            @PathVariable Long id,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        LabResult labResult =
                laboratoryService.getLabResultById(
                        id,
                        authenticatedUser
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labResult", new LabResultResponse(labResult)
                )
        );
    }

    @GetMapping("/patients/{patientId}/orders")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','LAB_TECHNICIAN','PATIENT')"
    )
    public ResponseEntity<?> getPatientLabOrders(
            @PathVariable Long patientId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<LabOrder> labOrders =
                laboratoryService.getPatientLabOrders(
                        patientId,
                        authenticatedUser
                );

        List<LabOrderResponse> response = labOrders.stream()
                .map(LabOrderResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labOrders", response
                )
        );
    }

    @GetMapping("/doctors/{doctorId}/orders")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','LAB_TECHNICIAN')"
    )
    public ResponseEntity<?> getDoctorLabOrders(
            @PathVariable Long doctorId) {

        List<LabOrder> labOrders =
                laboratoryService.getDoctorLabOrders(doctorId);

        List<LabOrderResponse> response = labOrders.stream()
                .map(LabOrderResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labOrders", response
                )
        );
    }

    @GetMapping("/orders/status/{status}")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','LAB_TECHNICIAN')"
    )
    public ResponseEntity<?> getLabOrdersByStatus(
            @PathVariable String status) {

        List<LabOrder> labOrders =
                laboratoryService.getLabOrdersByStatus(status);

        List<LabOrderResponse> response = labOrders.stream()
                .map(LabOrderResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labOrders", response
                )
        );
    }

    @GetMapping("/orders/{labOrderId}/results")
    @PreAuthorize(
            "hasAnyRole('ADMIN','DOCTOR','NURSE','LAB_TECHNICIAN','PATIENT')"
    )
    public ResponseEntity<?> getResultsForLabOrder(
            @PathVariable Long labOrderId,
            Authentication authentication) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        List<LabResult> labResults =
                laboratoryService.getResultsForLabOrder(
                        labOrderId,
                        authenticatedUser
                );

        List<LabResultResponse> response = labResults.stream()
                .map(LabResultResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labResults", response
                )
        );
    }

    @GetMapping("/technicians/{technicianId}/results")
    @PreAuthorize(
            "hasAnyRole('ADMIN','LAB_TECHNICIAN')"
    )
    public ResponseEntity<?> getTechnicianResults(
            @PathVariable Long technicianId) {

        List<LabResult> labResults =
                laboratoryService.getTechnicianResults(
                        technicianId
                );

        List<LabResultResponse> response = labResults.stream()
                .map(LabResultResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "labResults", response
                )
        );
    }

    /**
     * Resolve the currently authenticated application user
     * from the email stored in the JWT authentication.
     */
    private User getAuthenticatedUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Authenticated user not found"
                        ));
    }
}