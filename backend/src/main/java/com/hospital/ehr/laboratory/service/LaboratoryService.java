package com.hospital.ehr.laboratory.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.laboratory.dto.CreateLabOrderRequest;
import com.hospital.ehr.laboratory.dto.CreateLabResultRequest;
import com.hospital.ehr.laboratory.entity.LabOrder;
import com.hospital.ehr.laboratory.entity.LabResult;
import com.hospital.ehr.laboratory.repository.LabOrderRepository;
import com.hospital.ehr.laboratory.repository.LabResultRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.patient.service.PatientAccessService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LaboratoryService {

    private final LabOrderRepository labOrderRepository;
    private final LabResultRepository labResultRepository;
    private final PatientRepository patientRepository;
        private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public LaboratoryService(
            LabOrderRepository labOrderRepository,
            LabResultRepository labResultRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService) {

        this.labOrderRepository = labOrderRepository;
        this.labResultRepository = labResultRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public LabOrder createLabOrder(
            CreateLabOrderRequest request,
            Long doctorId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found."));

        boolean isDoctor = doctor.getRoles()
                .stream()
                .anyMatch(role ->
                        "DOCTOR".equals(role.getName())
                );

        if (!isDoctor) {
            throw new IllegalArgumentException(
                    "Selected user is not registered as a doctor."
            );
        }

        Patient patient = patientRepository.findById(
                request.getPatientId()
        ).orElseThrow(() ->
                new IllegalArgumentException("Patient not found."));

        patientAccessService.assertDoctorCanAccess(patient, doctor);

        String priority = request.getPriority();

        if (priority == null || priority.isBlank()) {
            priority = "ROUTINE";
        } else {
            priority = priority.trim().toUpperCase();
        }

        if (!priority.equals("ROUTINE")
                && !priority.equals("URGENT")
                && !priority.equals("STAT")) {

            throw new IllegalArgumentException(
                    "Priority must be ROUTINE, URGENT, or STAT."
            );
        }

        LabOrder labOrder = new LabOrder();

        labOrder.setPatient(patient);
        labOrder.setDoctor(doctor);
        labOrder.setTestName(request.getTestName().trim());

        if (request.getTestCode() != null
                && !request.getTestCode().isBlank()) {

            labOrder.setTestCode(
                    request.getTestCode().trim()
            );
        }

        labOrder.setPriority(priority);

        if (request.getClinicalNotes() != null
                && !request.getClinicalNotes().isBlank()) {

            labOrder.setClinicalNotes(
                    request.getClinicalNotes().trim()
            );
        }

        labOrder.setStatus("ORDERED");

        return labOrderRepository.save(labOrder);
    }

    @Transactional
    public LabResult createLabResult(
            Long labOrderId,
            CreateLabResultRequest request,
            Long technicianId) {

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab technician not found."
                        ));

        boolean isTechnician = technician.getRoles()
                .stream()
                .anyMatch(role ->
                        "LAB_TECHNICIAN".equals(role.getName())
                );

        if (!isTechnician) {
            throw new IllegalArgumentException(
                    "Selected user is not registered as a lab technician."
            );
        }

        LabOrder labOrder = labOrderRepository.findById(labOrderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab order not found."
                        ));

        if ("CANCELLED".equals(labOrder.getStatus())) {
            throw new IllegalArgumentException(
                    "Cannot add a result to a cancelled lab order."
            );
        }

        if ("COMPLETED".equals(labOrder.getStatus())) {
            throw new IllegalArgumentException(
                    "Lab order has already been completed."
            );
        }

        LabResult result = new LabResult();

        result.setLabOrder(labOrder);
        result.setLabTechnician(technician);

        result.setResultValue(
                request.getResultValue() == null
                        ? null
                        : request.getResultValue().trim()
        );

        result.setUnit(
                request.getUnit() == null
                        ? null
                        : request.getUnit().trim()
        );

        result.setReferenceRange(
                request.getReferenceRange() == null
                        ? null
                        : request.getReferenceRange().trim()
        );

        result.setInterpretation(
                request.getInterpretation() == null
                        ? null
                        : request.getInterpretation().trim()
        );

        result.setResultStatus("PRELIMINARY");
        result.setPerformedAt(LocalDateTime.now());

        LabResult savedResult =
                labResultRepository.save(result);

        labOrder.setStatus("RESULT_AVAILABLE");
        labOrderRepository.save(labOrder);

        return savedResult;
    }

    @Transactional(readOnly = true)
    public LabOrder getLabOrderById(
            Long id,
            User authenticatedUser) {

        LabOrder labOrder = labOrderRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab order not found."
                        ));

        validatePatientAccess(
                labOrder.getPatient(),
                authenticatedUser
        );

        return labOrder;
    }

    @Transactional(readOnly = true)
    public LabResult getLabResultById(
            Long id,
            User authenticatedUser) {

        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab result not found."
                        ));

        validatePatientAccess(
                labResult.getLabOrder().getPatient(),
                authenticatedUser
        );

        return labResult;
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getPatientLabOrders(
            Long patientId,
            User authenticatedUser) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient not found."
                        ));

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        return labOrderRepository
                .findByPatientOrderByOrderedAtDesc(patient);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getDoctorLabOrders(
            Long doctorId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Doctor not found."
                        ));

        return labOrderRepository
                .findByDoctorOrderByOrderedAtDesc(doctor);
    }

    @Transactional(readOnly = true)
    public List<LabOrder> getLabOrdersByStatus(
            String status) {

        return labOrderRepository
                .findByStatusOrderByOrderedAtAsc(
                        status.toUpperCase()
                );
    }

    @Transactional(readOnly = true)
    public List<LabResult> getResultsForLabOrder(
            Long labOrderId,
            User authenticatedUser) {

        LabOrder labOrder = labOrderRepository.findById(labOrderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab order not found."
                        ));

        validatePatientAccess(
                labOrder.getPatient(),
                authenticatedUser
        );

        return labResultRepository
                .findByLabOrderOrderByPerformedAtDesc(
                        labOrder
                );
    }

    @Transactional(readOnly = true)
    public List<LabResult> getTechnicianResults(
            Long technicianId) {

        User technician = userRepository.findById(technicianId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lab technician not found."
                        ));

        return labResultRepository
                .findByLabTechnicianOrderByPerformedAtDesc(
                        technician
                );
    }

    /**
     * Verifies whether the authenticated user is allowed
     * to access the requested patient's laboratory data.
     *
     * ADMIN, DOCTOR, NURSE and LAB_TECHNICIAN can access
     * patient laboratory records through the controller rules.
     *
     * PATIENT users can access only their own patient record.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }
}