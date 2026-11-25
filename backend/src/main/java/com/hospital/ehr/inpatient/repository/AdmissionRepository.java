package com.hospital.ehr.inpatient.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.inpatient.entity.Admission;
import com.hospital.ehr.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {

    List<Admission> findByPatientOrderByAdmissionDateDesc(Patient patient);

    List<Admission> findByAdmittingDoctorOrderByAdmissionDateDesc(User doctor);

    List<Admission> findByStatusOrderByAdmissionDateDesc(String status);

    List<Admission> findByAdmissionCategoryOrderByAdmissionDateDesc(String admissionCategory);

    List<Admission> findByPatientAndStatusOrderByAdmissionDateDesc(
            Patient patient,
            String status
    );

    List<Admission> findByAdmittingDoctorAndStatusOrderByAdmissionDateDesc(
            User doctor,
            String status
    );

    Optional<Admission> findByIdAndPatient(Long id, Patient patient);

    Optional<Admission> findByIdAndAdmittingDoctor(Long id, User doctor);

    boolean existsByPatientAndStatus(
            Patient patient,
            String status
    );
}