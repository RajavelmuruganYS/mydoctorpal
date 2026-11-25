package com.hospital.ehr.prescription.controller;

import com.hospital.ehr.prescription.dto.CreateDrugRequest;
import com.hospital.ehr.prescription.dto.DrugResponse;
import com.hospital.ehr.prescription.entity.Drug;
import com.hospital.ehr.prescription.service.DrugService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/drugs")
@SecurityRequirement(name = "bearerAuth")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<?> createDrug(
            @Valid @RequestBody CreateDrugRequest request
    ) {

        Drug drug = drugService.createDrug(request);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Drug created successfully.",
                        "drug", new DrugResponse(drug)
                )
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
    public ResponseEntity<?> getAllActiveDrugs() {

        List<DrugResponse> drugs = drugService
                .getAllActiveDrugs()
                .stream()
                .map(DrugResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "drugs", drugs
                )
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
    public ResponseEntity<?> getDrugById(
            @PathVariable Long id
    ) {

        Drug drug = drugService.getDrugById(id);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "drug", new DrugResponse(drug)
                )
        );
    }

    @GetMapping("/search/brand")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
    public ResponseEntity<?> searchByBrandName(
            @RequestParam String name
    ) {

        List<DrugResponse> drugs = drugService
                .searchByBrandName(name)
                .stream()
                .map(DrugResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "drugs", drugs
                )
        );
    }

    @GetMapping("/search/generic")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PHARMACIST')")
    public ResponseEntity<?> searchByGenericName(
            @RequestParam String name
    ) {

        List<DrugResponse> drugs = drugService
                .searchByGenericName(name)
                .stream()
                .map(DrugResponse::new)
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "drugs", drugs
                )
        );
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<?> deactivateDrug(
            @PathVariable Long id
    ) {

        Drug drug = drugService.deactivateDrug(id);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Drug deactivated successfully.",
                        "drug", new DrugResponse(drug)
                )
        );
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN','PHARMACIST')")
    public ResponseEntity<?> activateDrug(
            @PathVariable Long id
    ) {

        Drug drug = drugService.activateDrug(id);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Drug activated successfully.",
                        "drug", new DrugResponse(drug)
                )
        );
    }
}