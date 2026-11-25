package com.hospital.ehr.vitals.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.vitals.entity.Vital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VitalRepository extends JpaRepository<Vital, Long> {

    List<Vital> findByPatientOrderByRecordedAtDesc(Patient patient);

    List<Vital> findByRecordedByUserOrderByRecordedAtDesc(User user);

    Optional<Vital> findFirstByPatientOrderByRecordedAtDesc(Patient patient);

    List<Vital> findByPatientAndRecordedByUserOrderByRecordedAtDesc(
            Patient patient,
            User user
    );
}