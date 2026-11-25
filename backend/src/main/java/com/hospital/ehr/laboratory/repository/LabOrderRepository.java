package com.hospital.ehr.laboratory.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.laboratory.entity.LabOrder;
import com.hospital.ehr.patient.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabOrderRepository extends JpaRepository<LabOrder, Long> {

    List<LabOrder> findByPatientOrderByOrderedAtDesc(Patient patient);

    List<LabOrder> findByDoctorOrderByOrderedAtDesc(User doctor);

    List<LabOrder> findByStatusOrderByOrderedAtAsc(String status);

    List<LabOrder> findByPriorityOrderByOrderedAtAsc(String priority);

    List<LabOrder> findByPatientAndStatusOrderByOrderedAtDesc(
            Patient patient,
            String status
    );

    List<LabOrder> findByDoctorAndStatusOrderByOrderedAtDesc(
            User doctor,
            String status
    );
}