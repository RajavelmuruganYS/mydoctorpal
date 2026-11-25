package com.hospital.ehr.pharmacy.repository;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.pharmacy.entity.PrescriptionDispensation;
import com.hospital.ehr.prescription.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionDispensationRepository
        extends JpaRepository<PrescriptionDispensation, Long> {

    List<PrescriptionDispensation>
    findByPrescriptionOrderByDispensedAtDesc(Prescription prescription);

    List<PrescriptionDispensation>
    findByPharmacistOrderByDispensedAtDesc(User pharmacist);

    Optional<PrescriptionDispensation>
    findFirstByPrescriptionOrderByDispensedAtDesc(Prescription prescription);

    List<PrescriptionDispensation>
    findByStatusOrderByDispensedAtDesc(String status);
}