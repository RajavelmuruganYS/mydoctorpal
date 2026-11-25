package com.hospital.ehr.prescription.repository;

import com.hospital.ehr.prescription.entity.Drug;
import com.hospital.ehr.prescription.entity.Prescription;
import com.hospital.ehr.prescription.entity.PrescriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    List<PrescriptionItem> findByPrescription(Prescription prescription);

    List<PrescriptionItem> findByPrescriptionOrderByIdAsc(
            Prescription prescription
    );

    List<PrescriptionItem> findByDrug(Drug drug);

    void deleteByPrescription(Prescription prescription);
}