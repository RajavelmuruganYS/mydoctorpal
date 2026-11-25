package com.hospital.ehr.patient.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientDoctorAssignmentRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientAccessService {

    private final PatientDoctorAssignmentRepository assignmentRepository;

    public PatientAccessService(
            PatientDoctorAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public void assertCanAccess(Patient patient, User user) {
        if (patient == null || user == null) deny();
        if (hasRole(user, "ADMIN") || hasRole(user, "NURSE")
                || hasRole(user, "LAB_TECHNICIAN")
            || hasRole(user, "RADIOLOGY_TECHNICIAN")
                || hasRole(user, "PHARMACIST")) return;
        if (hasRole(user, "PATIENT")) {
            if (patient.getUser() != null
                    && user.getId().equals(patient.getUser().getId())) return;
            deny();
        }
        if (hasRole(user, "DOCTOR")
                && assignmentRepository.existsByPatientAndDoctorAndStatus(
                patient, user, "ACTIVE")) return;
        deny();
    }

    public void assertDoctorCanAccess(Patient patient, User doctor) {
        if (patient == null || doctor == null || !hasRole(doctor, "DOCTOR")
                || !assignmentRepository.existsByPatientAndDoctorAndStatus(
                patient, doctor, "ACTIVE")) deny();
    }

    private boolean hasRole(User user, String role) {
        return user.getRoles().stream().anyMatch(r -> role.equalsIgnoreCase(r.getName()));
    }

    private void deny() {
        throw new SecurityException("You are not authorized to access this patient's clinical records.");
    }
}
