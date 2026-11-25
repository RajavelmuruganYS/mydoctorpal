package com.hospital.ehr.inpatient.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.inpatient.dto.AdmissionResponse;
import com.hospital.ehr.inpatient.dto.CreateAdmissionRequest;
import com.hospital.ehr.inpatient.service.InpatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inpatient")
@SecurityRequirement(name = "bearerAuth")
public class InpatientController {

    private final InpatientService inpatientService;
    private final UserRepository userRepository;

    public InpatientController(
            InpatientService inpatientService,
            UserRepository userRepository
    ) {
        this.inpatientService = inpatientService;
        this.userRepository = userRepository;
    }

    @PostMapping("/admissions")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<AdmissionResponse> admitPatient(
            @Valid @RequestBody CreateAdmissionRequest request,
            Authentication authentication
    ) {

        User doctor = getAuthenticatedUser(authentication);

        AdmissionResponse response =
                inpatientService.admitPatient(
                        request,
                        doctor.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/admissions/{admissionId}/discharge")
    @PreAuthorize("hasAnyRole('DOCTOR', 'NURSE', 'ADMIN')")
    public ResponseEntity<AdmissionResponse> dischargePatient(
            @PathVariable Long admissionId
    ) {

        return ResponseEntity.ok(
                inpatientService.dischargePatient(admissionId)
        );
    }

    @GetMapping("/admissions/{admissionId}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'PATIENT'
            )
            """)
    public ResponseEntity<AdmissionResponse> getAdmission(
            @PathVariable Long admissionId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                inpatientService.getAdmissionById(
                        admissionId,
                        authenticatedUser
                )
        );
    }

    @GetMapping("/patients/{patientId}/admissions")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'PATIENT'
            )
            """)
    public ResponseEntity<List<AdmissionResponse>> getPatientAdmissions(
            @PathVariable Long patientId,
            Authentication authentication
    ) {

        User authenticatedUser =
                getAuthenticatedUser(authentication);

        return ResponseEntity.ok(
                inpatientService.getPatientAdmissions(
                        patientId,
                        authenticatedUser
                )
        );
    }

    @GetMapping("/doctors/{doctorId}/admissions")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<List<AdmissionResponse>> getDoctorAdmissions(
            @PathVariable Long doctorId
    ) {

        return ResponseEntity.ok(
                inpatientService.getDoctorAdmissions(doctorId)
        );
    }

    @GetMapping("/admissions/status/{status}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<List<AdmissionResponse>> getAdmissionsByStatus(
            @PathVariable String status
    ) {

        return ResponseEntity.ok(
                inpatientService.getAdmissionsByStatus(status)
        );
    }

    @GetMapping("/admissions/category/{category}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<List<AdmissionResponse>> getAdmissionsByCategory(
            @PathVariable String category
    ) {

        return ResponseEntity.ok(
                inpatientService.getAdmissionsByCategory(category)
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