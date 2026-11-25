package com.hospital.ehr.radiology.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.radiology.dto.AmendRadiologyReportRequest;
import com.hospital.ehr.radiology.dto.CreateRadiologyReportRequest;
import com.hospital.ehr.radiology.dto.RadiologyReportResponse;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.entity.RadiologyReport;
import com.hospital.ehr.radiology.repository.RadiologyOrderRepository;
import com.hospital.ehr.radiology.repository.RadiologyReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RadiologyReportService {

    private final RadiologyReportRepository radiologyReportRepository;
    private final RadiologyOrderRepository radiologyOrderRepository;
    private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public RadiologyReportService(
            RadiologyReportRepository radiologyReportRepository,
            RadiologyOrderRepository radiologyOrderRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.radiologyReportRepository = radiologyReportRepository;
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    public RadiologyReportResponse createReport(
            CreateRadiologyReportRequest request,
            Long reportedByUserId
    ) {

        RadiologyOrder order = radiologyOrderRepository
                .findById(request.getRadiologyOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        User reportedByUser = userRepository.findById(reportedByUserId)
                .orElseThrow(() ->
                        new RuntimeException("Reporting user not found"));

        if (!isRadiologyTechnician(reportedByUser)) {
            throw new RuntimeException(
                    "Only radiology technicians can create radiology reports"
            );
        }

        if ("CANCELLED".equals(order.getStatus())) {
            throw new RuntimeException(
                    "Cannot create a report for a cancelled radiology order"
            );
        }

        RadiologyReport report = new RadiologyReport();

        report.setRadiologyOrder(order);
        report.setReportedByUser(reportedByUser);
        report.setReportText(request.getReportText().trim());

        report.setFindings(
                request.getFindings() == null
                        ? null
                        : request.getFindings().trim()
        );

        report.setImpression(
                request.getImpression() == null
                        ? null
                        : request.getImpression().trim()
        );

        boolean criticalFinding =
                Boolean.TRUE.equals(request.getCriticalFinding());

        report.setCriticalFinding(criticalFinding);

        if (criticalFinding &&
                (request.getCriticalFindingNotes() == null ||
                        request.getCriticalFindingNotes().isBlank())) {

            throw new RuntimeException(
                    "Critical finding notes are required when critical finding is true"
            );
        }

        report.setCriticalFindingNotes(
                request.getCriticalFindingNotes() == null
                        ? null
                        : request.getCriticalFindingNotes().trim()
        );

        boolean comparedWithPriorStudy =
                Boolean.TRUE.equals(request.getComparedWithPriorStudy());

        report.setComparedWithPriorStudy(comparedWithPriorStudy);

        if (comparedWithPriorStudy &&
                (request.getPriorStudyReference() == null ||
                        request.getPriorStudyReference().isBlank())) {

            throw new RuntimeException(
                    "Prior study reference is required when comparing with a prior study"
            );
        }

        report.setPriorStudyReference(
                request.getPriorStudyReference() == null
                        ? null
                        : request.getPriorStudyReference().trim()
        );

        report.setStatus("DRAFT");

        RadiologyReport savedReport =
                radiologyReportRepository.save(report);

        return RadiologyReportResponse.fromEntity(savedReport);
    }

    public RadiologyReportResponse finalizeReport(
            Long reportId,
            Long userId
    ) {

        RadiologyReport report = radiologyReportRepository
                .findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology report not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!isRadiologyTechnician(user)) {
            throw new RuntimeException(
                    "Only radiology technicians can finalize radiology reports"
            );
        }

        if (!report.getReportedByUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Only the reporting doctor can finalize this report"
            );
        }

        if (!"DRAFT".equals(report.getStatus())) {
            throw new RuntimeException(
                    "Only draft reports can be finalized"
            );
        }

        report.setStatus("FINAL");
        report.setSignedAt(LocalDateTime.now());

        RadiologyReport savedReport =
                radiologyReportRepository.save(report);

        return RadiologyReportResponse.fromEntity(savedReport);
    }

    public RadiologyReportResponse amendReport(
            Long reportId,
            AmendRadiologyReportRequest request,
            Long userId
    ) {

        RadiologyReport report = radiologyReportRepository
                .findById(reportId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology report not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!isDoctor(user)) {
            throw new RuntimeException(
                    "Only doctors can amend radiology reports"
            );
        }

        if (!report.getReportedByUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "Only the reporting doctor can amend this report"
            );
        }

        if (!"FINAL".equals(report.getStatus()) &&
                !"AMENDED".equals(report.getStatus())) {

            throw new RuntimeException(
                    "Only finalized reports can be amended"
            );
        }

        report.setReportText(request.getReportText().trim());

        report.setFindings(
                request.getFindings() == null
                        ? null
                        : request.getFindings().trim()
        );

        report.setImpression(
                request.getImpression() == null
                        ? null
                        : request.getImpression().trim()
        );

        boolean criticalFinding =
                Boolean.TRUE.equals(request.getCriticalFinding());

        if (criticalFinding &&
                (request.getCriticalFindingNotes() == null ||
                        request.getCriticalFindingNotes().isBlank())) {

            throw new RuntimeException(
                    "Critical finding notes are required when critical finding is true"
            );
        }

        report.setCriticalFinding(criticalFinding);

        report.setCriticalFindingNotes(
                request.getCriticalFindingNotes() == null
                        ? null
                        : request.getCriticalFindingNotes().trim()
        );

        report.setStatus("AMENDED");
        report.setSignedAt(LocalDateTime.now());

        RadiologyReport savedReport =
                radiologyReportRepository.save(report);

        return RadiologyReportResponse.fromEntity(savedReport);
    }

    @Transactional(readOnly = true)
    public RadiologyReportResponse getReportById(
            Long id,
            User authenticatedUser
    ) {

        RadiologyReport report = radiologyReportRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Radiology report not found"));

        validatePatientAccess(
                report.getRadiologyOrder().getPatient(),
                authenticatedUser
        );

        return RadiologyReportResponse.fromEntity(report);
    }

    @Transactional(readOnly = true)
    public List<RadiologyReportResponse> getReportsByOrder(
            Long radiologyOrderId,
            User authenticatedUser
    ) {

        RadiologyOrder order = radiologyOrderRepository
                .findById(radiologyOrderId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        validatePatientAccess(
                order.getPatient(),
                authenticatedUser
        );

        return radiologyReportRepository
                .findByRadiologyOrderOrderByCreatedAtDesc(order)
                .stream()
                .map(RadiologyReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyReportResponse> getReportsByUser(
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return radiologyReportRepository
                .findByReportedByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(RadiologyReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyReportResponse> getReportsByStatus(
            String status
    ) {

        String normalizedStatus = status.trim().toUpperCase();

        if (!isValidStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid status. Allowed values: DRAFT, FINAL, AMENDED, CANCELLED"
            );
        }

        return radiologyReportRepository
                .findByStatusOrderByCreatedAtDesc(normalizedStatus)
                .stream()
                .map(RadiologyReportResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyReportResponse> getCriticalReports() {

        return radiologyReportRepository
                .findByCriticalFindingOrderByCreatedAtDesc(true)
                .stream()
                .map(RadiologyReportResponse::fromEntity)
                .toList();
    }

    /**
     * PATIENT users can access only their own radiology records.
     *
     * ADMIN, DOCTOR and NURSE access is controlled by
     * @PreAuthorize in the controller.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }

    private boolean isDoctor(User user) {

        return user.getRoles() != null &&
                user.getRoles().stream()
                        .anyMatch(role ->
                                "DOCTOR".equalsIgnoreCase(role.getName()));
    }

    private boolean isRadiologyTechnician(User user) {

        return user.getRoles() != null &&
                user.getRoles().stream()
                        .anyMatch(role ->
                                "RADIOLOGY_TECHNICIAN".equalsIgnoreCase(role.getName()));
    }

    private boolean isValidStatus(String status) {

        return switch (status) {
            case "DRAFT", "FINAL", "AMENDED", "CANCELLED" -> true;
            default -> false;
        };
    }
}