package com.hospital.ehr.patient.service;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientServiceAssignmentTest {
    @Mock private PatientRepository patientRepository;
    @Mock private UserRepository userRepository;
    @Mock private PatientAccessService patientAccessService;
    @Mock private PatientDoctorAssignmentRepository assignmentRepository;

    @Test
    void returnsOnlyActiveAssignmentQueryResults() {
        User doctor = new User();
        Patient patient = new Patient();
        PatientDoctorAssignment assignment = new PatientDoctorAssignment();
        assignment.setPatient(patient);
        when(assignmentRepository.findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE"))
                .thenReturn(List.of(assignment));

        PatientService service = service();

        assertEquals(List.of(patient), service.getAssignedPatients(doctor));
        verify(assignmentRepository).findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE");
    }

    @Test
    void returnsNoPatientsWhenDoctorHasNoActiveAssignments() {
        User doctor = new User();
        when(assignmentRepository.findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE"))
                .thenReturn(List.of());

        assertEquals(List.of(), service().getAssignedPatients(doctor));
    }

    @Test
    void revokedAssignmentsAreExcludedByActiveStatusQuery() {
        User doctor = new User();
        when(assignmentRepository.findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE"))
                .thenReturn(List.of());

        assertEquals(0, service().getAssignedPatients(doctor).size());
        verify(assignmentRepository).findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE");
    }

    private PatientService service() {
        return new PatientService(patientRepository, userRepository, patientAccessService, assignmentRepository);
    }
}
