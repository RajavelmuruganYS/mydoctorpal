package com.hospital.ehr.prescription.repository;

import com.hospital.ehr.prescription.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, Long> {

    List<Drug> findByActiveTrueOrderByBrandNameAsc();

    List<Drug> findByBrandNameContainingIgnoreCaseAndActiveTrue(
            String brandName
    );

    List<Drug> findByGenericNameContainingIgnoreCaseAndActiveTrue(
            String genericName
    );

    Optional<Drug> findByBrandNameIgnoreCaseAndGenericNameIgnoreCase(
            String brandName,
            String genericName
    );
}