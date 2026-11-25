package com.hospital.ehr.patient.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.entity.PatientDoctorAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface PatientDoctorAssignmentRepository
        extends JpaRepository<PatientDoctorAssignment, Long> {

    Optional<PatientDoctorAssignment> findByPatientAndDoctor(
            Patient patient, User doctor);

    boolean existsByPatientAndDoctorAndStatus(
            Patient patient, User doctor, String status);

    @EntityGraph(attributePaths = {"doctor", "patient", "patient.user"})
    List<PatientDoctorAssignment> findByPatientOrderByAssignedAtDesc(
            Patient patient);

    @EntityGraph(attributePaths = {"doctor", "patient", "patient.user"})
    List<PatientDoctorAssignment> findByPatientAndStatusOrderByAssignedAtDesc(
            Patient patient, String status);

    @EntityGraph(attributePaths = {"doctor", "patient", "patient.user"})
    List<PatientDoctorAssignment> findByDoctorAndStatusOrderByAssignedAtDesc(
            User doctor, String status);
}
