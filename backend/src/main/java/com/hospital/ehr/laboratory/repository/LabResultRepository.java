package com.hospital.ehr.laboratory.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.laboratory.entity.LabOrder;
import com.hospital.ehr.laboratory.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabResultRepository extends JpaRepository<LabResult, Long> {

    List<LabResult> findByLabOrderOrderByPerformedAtDesc(
            LabOrder labOrder
    );

    List<LabResult> findByLabTechnicianOrderByPerformedAtDesc(
            User labTechnician
    );

    List<LabResult> findByResultStatusOrderByPerformedAtDesc(
            String resultStatus
    );

    Optional<LabResult> findFirstByLabOrderOrderByPerformedAtDesc(
            LabOrder labOrder
    );
}