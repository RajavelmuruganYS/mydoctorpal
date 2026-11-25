package com.hospital.ehr.radiology.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.entity.RadiologyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RadiologyReportRepository
        extends JpaRepository<RadiologyReport, Long> {

    List<RadiologyReport> findByRadiologyOrderOrderByCreatedAtDesc(
            RadiologyOrder radiologyOrder
    );

    List<RadiologyReport> findByReportedByUserOrderByCreatedAtDesc(
            User reportedByUser
    );

    List<RadiologyReport> findByStatusOrderByCreatedAtDesc(
            String status
    );

    List<RadiologyReport> findByCriticalFindingOrderByCreatedAtDesc(
            Boolean criticalFinding
    );

    Optional<RadiologyReport> findByIdAndReportedByUser(
            Long id,
            User reportedByUser
    );

    Optional<RadiologyReport> findFirstByRadiologyOrderOrderByCreatedAtDesc(
            RadiologyOrder radiologyOrder
    );
}