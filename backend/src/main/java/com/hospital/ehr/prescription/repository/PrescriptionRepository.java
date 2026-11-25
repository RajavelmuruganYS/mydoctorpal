package com.hospital.ehr.prescription.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientOrderByPrescriptionDateDesc(
            Patient patient
    );

    List<Prescription> findByDoctorOrderByPrescriptionDateDesc(
            User doctor
    );

    List<Prescription> findByPatientAndStatusOrderByPrescriptionDateDesc(
            Patient patient,
            String status
    );

    List<Prescription> findByDoctorAndStatusOrderByPrescriptionDateDesc(
            User doctor,
            String status
    );

    List<Prescription> findByDispenseStatusOrderByPrescriptionDateAsc(
            String dispenseStatus
    );

    List<Prescription> findByExpiresAtBeforeAndStatus(
            LocalDate date,
            String status
    );
}