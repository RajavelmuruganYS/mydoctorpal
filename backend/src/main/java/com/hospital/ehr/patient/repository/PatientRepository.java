package com.hospital.ehr.patient.repository;

import com.hospital.ehr.patient.entity.Patient;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*
 * The linked User is fetched together with the Patient so that
 * PatientResponse can expose the patient's name without a
 * LazyInitializationException (open-in-view is disabled).
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Override
    @EntityGraph(attributePaths = "user")
    List<Patient> findAll();

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<Patient> findById(Long id);

    @EntityGraph(attributePaths = "user")
    Optional<Patient> findByUserId(Long userId);

    @EntityGraph(attributePaths = "user")
    Optional<Patient> findByPatientNumber(String patientNumber);

    boolean existsByPatientNumber(String patientNumber);

    boolean existsByUserId(Long userId);
}
