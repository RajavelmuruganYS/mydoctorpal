package com.hospital.ehr.radiology.repository;

import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.entity.RadiologyStudy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RadiologyStudyRepository
        extends JpaRepository<RadiologyStudy, Long> {

    List<RadiologyStudy> findByRadiologyOrderOrderByCreatedAtDesc(
            RadiologyOrder radiologyOrder
    );

    Optional<RadiologyStudy> findByAccessionNumber(
            String accessionNumber
    );

    Optional<RadiologyStudy> findByStudyInstanceUid(
            String studyInstanceUid
    );

    List<RadiologyStudy> findByStudyStatusOrderByCreatedAtDesc(
            String studyStatus
    );

    Optional<RadiologyStudy> findFirstByRadiologyOrderOrderByCreatedAtDesc(
            RadiologyOrder radiologyOrder
    );
}