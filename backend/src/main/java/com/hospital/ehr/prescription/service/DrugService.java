package com.hospital.ehr.prescription.service;

import com.hospital.ehr.prescription.dto.CreateDrugRequest;
import com.hospital.ehr.prescription.entity.Drug;
import com.hospital.ehr.prescription.repository.DrugRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DrugService {

    private final DrugRepository drugRepository;

    public DrugService(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @Transactional
    public Drug createDrug(CreateDrugRequest request) {

        Drug drug = new Drug();

        drug.setBrandName(request.getBrandName().trim());
        drug.setGenericName(request.getGenericName().trim());
        drug.setDosageForm(request.getDosageForm().trim());
        drug.setStrength(request.getStrength().trim());
        drug.setActive(true);

        return drugRepository.save(drug);
    }

    public Drug getDrugById(Long id) {

        return drugRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Drug not found."
                        )
                );
    }

    public List<Drug> getAllActiveDrugs() {

        return drugRepository.findByActiveTrueOrderByBrandNameAsc();
    }

    public List<Drug> searchByBrandName(String brandName) {

        if (brandName == null || brandName.isBlank()) {
            return getAllActiveDrugs();
        }

        return drugRepository
                .findByBrandNameContainingIgnoreCaseAndActiveTrue(
                        brandName.trim()
                );
    }

    public List<Drug> searchByGenericName(String genericName) {

        if (genericName == null || genericName.isBlank()) {
            return getAllActiveDrugs();
        }

        return drugRepository
                .findByGenericNameContainingIgnoreCaseAndActiveTrue(
                        genericName.trim()
                );
    }

    @Transactional
    public Drug deactivateDrug(Long id) {

        Drug drug = getDrugById(id);

        drug.setActive(false);

        return drugRepository.save(drug);
    }

    @Transactional
    public Drug activateDrug(Long id) {

        Drug drug = getDrugById(id);

        drug.setActive(true);

        return drugRepository.save(drug);
    }
}