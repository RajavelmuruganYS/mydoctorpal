package com.hospital.ehr.patient.service;

import com.hospital.ehr.audit.service.AuditLogService;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.entity.PatientDoctorAssignment;
import com.hospital.ehr.patient.repository.PatientDoctorAssignmentRepository;
import com.hospital.ehr.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientDoctorAssignmentService {
    private final PatientDoctorAssignmentRepository assignmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public PatientDoctorAssignmentService(PatientDoctorAssignmentRepository assignmentRepository,
                                           PatientRepository patientRepository,
                                           UserRepository userRepository,
                                           AuditLogService auditLogService) {
        this.assignmentRepository = assignmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    public List<PatientDoctorAssignment> getForPatient(Long patientId) {
        return assignmentRepository.findByPatientAndStatusOrderByAssignedAtDesc(
                patient(patientId), "ACTIVE");
    }

    @Transactional
    public PatientDoctorAssignment assign(Long patientId, Long doctorId, Long adminId) {
        Patient patient = patient(patientId);
        User doctor = doctor(doctorId);
        PatientDoctorAssignment assignment = assignmentRepository.findByPatientAndDoctor(patient, doctor)
                .orElseGet(PatientDoctorAssignment::new);
        assignment.setPatient(patient);
        assignment.setDoctor(doctor);
        assignment.setStatus("ACTIVE");
        assignment.setEndedAt(null);
        PatientDoctorAssignment saved = assignmentRepository.save(assignment);
        auditLogService.log(adminId, patientId, "DOCTOR_ASSIGNED", "PATIENT_DOCTOR_ASSIGNMENT",
                String.valueOf(saved.getId()), "Assigned doctor to patient", null, null);
        return saved;
    }

    @Transactional
    public void revoke(Long patientId, Long doctorId, Long adminId) {
        PatientDoctorAssignment assignment = assignmentRepository
                .findByPatientAndDoctor(patient(patientId), doctor(doctorId))
                .orElseThrow(() -> new IllegalArgumentException("Doctor assignment not found."));
        assignment.setStatus("REVOKED");
        assignment.setEndedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);
        auditLogService.log(adminId, patientId, "DOCTOR_ASSIGNMENT_REVOKED", "PATIENT_DOCTOR_ASSIGNMENT",
                String.valueOf(assignment.getId()), "Revoked doctor assignment", null, null);
    }

    private Patient patient(Long id) {
        return patientRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Patient not found."));
    }

    private User doctor(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Doctor not found."));
        if (user.getRoles().stream().noneMatch(role -> "DOCTOR".equals(role.getName()))) {
            throw new IllegalArgumentException("Selected user is not a doctor.");
        }
        return user;
    }
}
