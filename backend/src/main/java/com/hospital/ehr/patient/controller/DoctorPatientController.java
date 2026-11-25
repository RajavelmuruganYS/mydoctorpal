package com.hospital.ehr.patient.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.dto.PatientResponse;
import com.hospital.ehr.patient.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors/me")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorPatientController {
    private final UserRepository userRepository;
    private final PatientService patientService;

    public DoctorPatientController(UserRepository userRepository,
                                   PatientService patientService) {
        this.userRepository = userRepository;
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponse>> getAssignedPatients(Authentication authentication) {
        User doctor = userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
                new IllegalArgumentException("Authenticated user not found."));
        List<PatientResponse> patients = patientService.getAssignedPatients(doctor)
            .stream()
                .map(PatientResponse::from)
                .toList();
        return ResponseEntity.ok(patients);
    }
}
