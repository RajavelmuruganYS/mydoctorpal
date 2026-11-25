package com.hospital.ehr.patient.service;

import com.hospital.ehr.audit.service.AuditLogService;
import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.entity.PatientDoctorAssignment;
import com.hospital.ehr.patient.repository.PatientDoctorAssignmentRepository;
import com.hospital.ehr.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientDoctorAssignmentServiceTest {
    @Mock private PatientDoctorAssignmentRepository assignmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditLogService auditLogService;

    @Test
    void getForPatientQueriesOnlyActiveAssignments() {
        Patient patient = new Patient();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(assignmentRepository.findByPatientAndStatusOrderByAssignedAtDesc(patient, "ACTIVE"))
                .thenReturn(List.of());

        assertEquals(List.of(), service().getForPatient(1L));
        verify(assignmentRepository).findByPatientAndStatusOrderByAssignedAtDesc(patient, "ACTIVE");
        verify(assignmentRepository, never()).findByPatientOrderByAssignedAtDesc(any());
    }

    @Test
    void revokedAssignmentCanBeReactivatedWithoutCreatingDuplicate() {
        Patient patient = new Patient();
        User doctor = doctor();
        PatientDoctorAssignment revoked = new PatientDoctorAssignment();
        revoked.setStatus("REVOKED");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(assignmentRepository.findByPatientAndDoctor(patient, doctor)).thenReturn(Optional.of(revoked));
        when(assignmentRepository.save(revoked)).thenReturn(revoked);

        PatientDoctorAssignment result = service().assign(1L, 2L, 9L);

        assertEquals("ACTIVE", result.getStatus());
        verify(assignmentRepository).save(revoked);
    }

    private PatientDoctorAssignmentService service() {
        return new PatientDoctorAssignmentService(
                assignmentRepository, patientRepository, userRepository, auditLogService);
    }

    private User doctor() {
        User doctor = new User();
        Role role = new Role();
        role.setName("DOCTOR");
        doctor.getRoles().add(role);
        return doctor;
    }
}
