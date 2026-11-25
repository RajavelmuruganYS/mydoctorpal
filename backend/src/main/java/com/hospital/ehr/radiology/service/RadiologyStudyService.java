package com.hospital.ehr.radiology.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.radiology.dto.CreateRadiologyStudyRequest;
import com.hospital.ehr.radiology.dto.RadiologyStudyResponse;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.entity.RadiologyStudy;
import com.hospital.ehr.radiology.repository.RadiologyOrderRepository;
import com.hospital.ehr.radiology.repository.RadiologyStudyRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RadiologyStudyService {

    private final RadiologyStudyRepository radiologyStudyRepository;
    private final RadiologyOrderRepository radiologyOrderRepository;
        private final PatientAccessService patientAccessService;

    public RadiologyStudyService(
            RadiologyStudyRepository radiologyStudyRepository,
            RadiologyOrderRepository radiologyOrderRepository,
            PatientAccessService patientAccessService
    ) {
        this.radiologyStudyRepository = radiologyStudyRepository;
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.patientAccessService = patientAccessService;
    }

    public RadiologyStudyResponse createStudy(
            CreateRadiologyStudyRequest request
    ) {

        RadiologyOrder order = radiologyOrderRepository
                .findById(request.getRadiologyOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Cannot create a study for a cancelled radiology order"
            );
        }

        String accessionNumber =
                request.getAccessionNumber().trim();

        if (radiologyStudyRepository
                .findByAccessionNumber(accessionNumber)
                .isPresent()) {

            throw new RuntimeException(
                    "Accession number already exists"
            );
        }

        String storageType = request.getStorageType();

        if (storageType == null || storageType.isBlank()) {
            storageType = "PACS";
        } else {
            storageType = storageType.trim().toUpperCase();
        }

        if (!isValidStorageType(storageType)) {
            throw new RuntimeException(
                    "Invalid storage type. Allowed values: PACS, DICOM_SERVER, LOCAL, EXTERNAL"
            );
        }

        RadiologyStudy study = new RadiologyStudy();

        study.setRadiologyOrder(order);
        study.setAccessionNumber(accessionNumber);

        study.setStudyInstanceUid(
                request.getStudyInstanceUid() == null
                        ? null
                        : request.getStudyInstanceUid().trim()
        );

        study.setSeriesCount(request.getSeriesCount());
        study.setImageCount(request.getImageCount());

        study.setStorageType(storageType);

        study.setStorageReference(
                request.getStorageReference() == null
                        ? null
                        : request.getStorageReference().trim()
        );

        study.setViewerUrl(
                request.getViewerUrl() == null
                        ? null
                        : request.getViewerUrl().trim()
        );

        study.setStudyStatus("AVAILABLE");

        study.setPerformedAt(LocalDateTime.now());

        RadiologyStudy savedStudy =
                radiologyStudyRepository.save(study);

        return RadiologyStudyResponse.fromEntity(savedStudy);
    }

    @Transactional(readOnly = true)
    public RadiologyStudyResponse getStudyById(
            Long id,
            User authenticatedUser
    ) {

        RadiologyStudy study = radiologyStudyRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Radiology study not found"));

        validatePatientAccess(
                study.getRadiologyOrder(),
                authenticatedUser
        );

        return RadiologyStudyResponse.fromEntity(study);
    }

    @Transactional(readOnly = true)
    public List<RadiologyStudyResponse> getStudiesByOrder(
            Long radiologyOrderId,
            User authenticatedUser
    ) {

        RadiologyOrder order = radiologyOrderRepository
                .findById(radiologyOrderId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        validatePatientAccess(
                order,
                authenticatedUser
        );

        return radiologyStudyRepository
                .findByRadiologyOrderOrderByCreatedAtDesc(order)
                .stream()
                .map(RadiologyStudyResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RadiologyStudyResponse getStudyByAccessionNumber(
            String accessionNumber,
            User authenticatedUser
    ) {

        RadiologyStudy study = radiologyStudyRepository
                .findByAccessionNumber(accessionNumber.trim())
                .orElseThrow(() ->
                        new RuntimeException("Radiology study not found"));

        validatePatientAccess(
                study.getRadiologyOrder(),
                authenticatedUser
        );

        return RadiologyStudyResponse.fromEntity(study);
    }

    @Transactional(readOnly = true)
    public RadiologyStudyResponse getStudyByInstanceUid(
            String studyInstanceUid,
            User authenticatedUser
    ) {

        RadiologyStudy study = radiologyStudyRepository
                .findByStudyInstanceUid(studyInstanceUid.trim())
                .orElseThrow(() ->
                        new RuntimeException("Radiology study not found"));

        validatePatientAccess(
                study.getRadiologyOrder(),
                authenticatedUser
        );

        return RadiologyStudyResponse.fromEntity(study);
    }

    @Transactional(readOnly = true)
    public List<RadiologyStudyResponse> getStudiesByStatus(
            String status
    ) {

        String normalizedStatus = status.trim().toUpperCase();

        if (!isValidStudyStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid study status. Allowed values: " +
                    "PENDING, AVAILABLE, ARCHIVED, FAILED"
            );
        }

        return radiologyStudyRepository
                .findByStudyStatusOrderByCreatedAtDesc(normalizedStatus)
                .stream()
                .map(RadiologyStudyResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RadiologyStudyResponse getLatestStudyForOrder(
            Long radiologyOrderId,
            User authenticatedUser
    ) {

        RadiologyOrder order = radiologyOrderRepository
                .findById(radiologyOrderId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        validatePatientAccess(
                order,
                authenticatedUser
        );

        RadiologyStudy study = radiologyStudyRepository
                .findFirstByRadiologyOrderOrderByCreatedAtDesc(order)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No radiology study found for this order"
                        ));

        return RadiologyStudyResponse.fromEntity(study);
    }

    /**
     * PATIENT users can access only studies belonging
     * to their own patient record.
     *
     * ADMIN, DOCTOR and NURSE access is controlled
     * by @PreAuthorize in the controller.
     */
    private void validatePatientAccess(
            RadiologyOrder order,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(order.getPatient(), authenticatedUser);
    }

    private boolean isValidStorageType(String storageType) {

        return switch (storageType) {
            case "PACS",
                 "DICOM_SERVER",
                 "LOCAL",
                 "EXTERNAL" -> true;

            default -> false;
        };
    }

    private boolean isValidStudyStatus(String status) {

        return switch (status) {
            case "PENDING",
                 "AVAILABLE",
                 "ARCHIVED",
                 "FAILED" -> true;

            default -> false;
        };
    }
}