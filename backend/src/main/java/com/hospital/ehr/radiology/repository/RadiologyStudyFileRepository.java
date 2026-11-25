package com.hospital.ehr.radiology.repository;

import com.hospital.ehr.radiology.entity.RadiologyStudy;
import com.hospital.ehr.radiology.entity.RadiologyStudyFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RadiologyStudyFileRepository extends JpaRepository<RadiologyStudyFile, Long> {

    List<RadiologyStudyFile> findByStudyOrderByUploadedAtDesc(RadiologyStudy study);

    Optional<RadiologyStudyFile> findByStudyAndId(RadiologyStudy study, Long id);
}
