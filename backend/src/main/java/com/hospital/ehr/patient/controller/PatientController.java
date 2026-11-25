package com.hospital.ehr.patient.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.dto.CreatePatientRequest;
import com.hospital.ehr.patient.dto.PatientResponse;
import com.hospital.ehr.patient.dto.UpdatePatientRequest;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.service.PatientService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/patients")
@SecurityRequirement(name = "bearerAuth")
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;

    public PatientController(
            PatientService patientService,
            UserRepository userRepository
    ) {
        this.patientService = patientService;
        this.userRepository = userRepository;
    }

    /**
     * Get the currently authenticated patient's own record.
     *
     * Only PATIENT users can access this endpoint.
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<?> getMyPatientRecord(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found."
                        )
                );

        Patient patient =
                patientService.getPatientByUserId(user.getId());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "patient", PatientResponse.from(patient)
                )
        );
    }

    /**
     * Get all patients.
     *
     * Accessible by Admin, Doctor and Nurse.
     */
    @GetMapping
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<?> getAllPatients() {

        List<Patient> patients =
                patientService.getAllPatients();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "count", patients.size(),
                        "patients", patients.stream().map(PatientResponse::from).toList()
                )
        );
    }

    /**
     * Get a patient by patient ID.
     *
     * Accessible by Admin, Doctor, Nurse,
     * Laboratory Technician and Pharmacist.
     */
    @GetMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'LAB_TECHNICIAN',
                'PHARMACIST'
            )
            """)
    public ResponseEntity<?> getPatientById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Patient patient =
                patientService.getPatientById(id, currentUser(authentication));

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "patient", PatientResponse.from(patient)
                )
        );
    }

        private User currentUser(Authentication authentication) {
                return userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
                                new IllegalArgumentException("Authenticated user not found."));
        }

    /**
     * Get a patient by hospital patient number.
     */
    @GetMapping("/number/{patientNumber}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE',
                'LAB_TECHNICIAN',
                'PHARMACIST'
            )
            """)
    public ResponseEntity<?> getPatientByNumber(
            @PathVariable String patientNumber,
            Authentication authentication
    ) {

        Patient patient =
                patientService.getPatientByNumber(patientNumber, currentUser(authentication));

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "patient", PatientResponse.from(patient)
                )
        );
    }

    /**
     * Create a patient record.
     *
     * Only Admin can create patient records.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {

        Patient patient = patientService.createPatient(
                request.getUserId(),
                request.getPatientNumber(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContactName(),
                request.getEmergencyContactPhone(),
                request.getMedicalHistory(),
                request.getAllergies()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Patient created successfully.",
                        "patient", PatientResponse.from(patient)
                )
        );
    }

    /**
     * Update an existing patient record.
     *
     * Admin, Doctor and Nurse can update
     * patient information.
     *
     * The version supplied by the client must match
     * the current patient record version.
     */
    @PutMapping("/{id}")
    @PreAuthorize("""
            hasAnyRole(
                'ADMIN',
                'DOCTOR',
                'NURSE'
            )
            """)
    public ResponseEntity<?> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest request
    ) {

        Patient patient = patientService.updatePatient(
                id,
                request.getDateOfBirth(),
                request.getGender(),
                request.getBloodGroup(),
                request.getAddress(),
                request.getEmergencyContactName(),
                request.getEmergencyContactPhone(),
                request.getMedicalHistory(),
                request.getAllergies(),
                request.getVersion()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Patient updated successfully.",
                        "patient", PatientResponse.from(patient)
                )
        );
    }

    /**
     * Delete a patient record.
     *
     * Only Admin can delete patient records.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePatient(
            @PathVariable Long id
    ) {

        patientService.deletePatient(id);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Patient deleted successfully."
                )
        );
    }
}