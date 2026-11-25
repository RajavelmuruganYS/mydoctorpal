package com.hospital.ehr.radiology.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.radiology.dto.CreateRadiologyOrderRequest;
import com.hospital.ehr.radiology.dto.RadiologyOrderResponse;
import com.hospital.ehr.radiology.dto.UpdateRadiologyOrderStatusRequest;
import com.hospital.ehr.radiology.service.RadiologyOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/radiology/orders")
@SecurityRequirement(name = "bearerAuth")
public class RadiologyOrderController {

    private final RadiologyOrderService radiologyOrderService;
    private final UserRepository userRepository;

    public RadiologyOrderController(
            RadiologyOrderService radiologyOrderService,
            UserRepository userRepository
    ) {
        this.radiologyOrderService = radiologyOrderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<RadiologyOrderResponse> createOrder(
            @Valid @RequestBody CreateRadiologyOrderRequest request,
            Authentication authentication
    ) {

        User doctor = getAuthenticatedUser(authentication);

        RadiologyOrderResponse response =
                radiologyOrderService.createOrder(
                        request,
                        doctor.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

        @PutMapping("/{orderId}/status")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<RadiologyOrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateRadiologyOrderStatusRequest request
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.updateStatus(
                        orderId,
                        request.getStatus()
                )
        );
    }

        @GetMapping("/{id}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<RadiologyOrderResponse> getOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyOrderService.getOrderById(
                        id,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/patients/{patientId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<List<RadiologyOrderResponse>> getPatientOrders(
            @PathVariable Long patientId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyOrderService.getPatientOrders(
                        patientId,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/patients/{patientId}/status/{status}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN', 'PATIENT')")
    public ResponseEntity<List<RadiologyOrderResponse>> getPatientOrdersByStatus(
            @PathVariable Long patientId,
            @PathVariable String status,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                radiologyOrderService.getPatientOrdersByStatus(
                        patientId,
                        status,
                        authenticatedUser
                )
        );
    }

        @GetMapping("/doctors/{doctorId}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyOrderResponse>> getDoctorOrders(
            @PathVariable Long doctorId
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.getDoctorOrders(doctorId)
        );
    }

        @GetMapping("/doctors/{doctorId}/status/{status}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyOrderResponse>> getDoctorOrdersByStatus(
            @PathVariable Long doctorId,
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.getDoctorOrdersByStatus(
                        doctorId,
                        status
                )
        );
    }

        @GetMapping("/status/{status}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyOrderResponse>> getOrdersByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.getOrdersByStatus(status)
        );
    }

        @GetMapping("/modality/{modality}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyOrderResponse>> getOrdersByModality(
            @PathVariable String modality
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.getOrdersByModality(modality)
        );
    }

        @GetMapping("/urgency/{urgency}")
                @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'RADIOLOGY_TECHNICIAN')")
    public ResponseEntity<List<RadiologyOrderResponse>> getOrdersByUrgency(
            @PathVariable String urgency
    ) {

        return ResponseEntity.ok(
                radiologyOrderService.getOrdersByUrgency(urgency)
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