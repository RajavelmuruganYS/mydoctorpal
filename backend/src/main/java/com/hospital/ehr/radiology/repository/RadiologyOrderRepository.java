package com.hospital.ehr.radiology.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RadiologyOrderRepository extends JpaRepository<RadiologyOrder, Long> {

    List<RadiologyOrder> findByPatientOrderByCreatedAtDesc(Patient patient);

    List<RadiologyOrder> findByOrderingDoctorOrderByCreatedAtDesc(User orderingDoctor);

    List<RadiologyOrder> findByStatusOrderByCreatedAtDesc(String status);

    List<RadiologyOrder> findByModalityOrderByCreatedAtDesc(String modality);

    List<RadiologyOrder> findByUrgencyOrderByCreatedAtDesc(String urgency);

    List<RadiologyOrder> findByPatientAndStatusOrderByCreatedAtDesc(
            Patient patient,
            String status
    );

    List<RadiologyOrder> findByOrderingDoctorAndStatusOrderByCreatedAtDesc(
            User orderingDoctor,
            String status
    );

    Optional<RadiologyOrder> findByIdAndPatient(
            Long id,
            Patient patient
    );

    Optional<RadiologyOrder> findByIdAndOrderingDoctor(
            Long id,
            User orderingDoctor
    );
}