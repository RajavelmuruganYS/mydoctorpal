package com.hospital.ehr.patient.service;

import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientDoctorAssignmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientAccessServiceTest {
    @Mock
    private PatientDoctorAssignmentRepository assignments;

    @Test
    void assignedDoctorCanAccessPatient() {
        User doctor = user(7L, "DOCTOR");
        Patient patient = patient(11L);
        when(assignments.existsByPatientAndDoctorAndStatus(patient, doctor, "ACTIVE"))
                .thenReturn(true);

        PatientAccessService service = new PatientAccessService(assignments);

        assertDoesNotThrow(() -> service.assertCanAccess(patient, doctor));
    }

    @Test
    void unassignedDoctorIsDenied() {
        User doctor = user(7L, "DOCTOR");
        Patient patient = patient(11L);
        when(assignments.existsByPatientAndDoctorAndStatus(patient, doctor, "ACTIVE"))
                .thenReturn(false);

        PatientAccessService service = new PatientAccessService(assignments);

        assertThrows(SecurityException.class, () -> service.assertCanAccess(patient, doctor));
    }

    @Test
    void patientCanAccessOnlyOwnRecord() {
        User account = user(7L, "PATIENT");
        Patient patient = patient(11L);
        patient.setUser(account);
        Patient other = patient(12L);
        other.setUser(user(8L, "PATIENT"));
        PatientAccessService service = new PatientAccessService(assignments);

        assertDoesNotThrow(() -> service.assertCanAccess(patient, account));
        assertThrows(SecurityException.class, () -> service.assertCanAccess(other, account));
    }

    private User user(Long id, String roleName) {
        User user = new User();
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
        Role role = new Role();
        role.setName(roleName);
        user.getRoles().add(role);
        return user;
    }

    private Patient patient(Long id) {
        Patient patient = new Patient();
        patient.setId(id);
        return patient;
    }
}
