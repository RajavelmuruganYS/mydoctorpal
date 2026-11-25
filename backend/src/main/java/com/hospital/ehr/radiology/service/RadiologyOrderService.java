package com.hospital.ehr.radiology.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.radiology.dto.CreateRadiologyOrderRequest;
import com.hospital.ehr.radiology.dto.RadiologyOrderResponse;
import com.hospital.ehr.radiology.entity.RadiologyOrder;
import com.hospital.ehr.radiology.repository.RadiologyOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RadiologyOrderService {

    private final RadiologyOrderRepository radiologyOrderRepository;
    private final PatientRepository patientRepository;
        private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public RadiologyOrderService(
            RadiologyOrderRepository radiologyOrderRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.radiologyOrderRepository = radiologyOrderRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public RadiologyOrderResponse createOrder(
            CreateRadiologyOrderRequest request,
            Long doctorId
    ) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() ->
                        new RuntimeException("Patient not found"));

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        boolean isDoctor = doctor.getRoles() == null ||
                doctor.getRoles().stream()
                        .noneMatch(role ->
                                "DOCTOR".equalsIgnoreCase(role.getName()));

        if (isDoctor) {
            throw new RuntimeException(
                    "Only doctors can create radiology orders"
            );
        }

        patientAccessService.assertDoctorCanAccess(patient, doctor);

        String modality = request.getModality()
                .trim()
                .toUpperCase();

        if (!isValidModality(modality)) {
            throw new RuntimeException(
                    "Invalid modality. Allowed values: XRAY, CT, MRI, ULTRASOUND"
            );
        }

        String urgency = request.getUrgency();

        if (urgency == null || urgency.isBlank()) {
            urgency = "ROUTINE";
        } else {
            urgency = urgency.trim().toUpperCase();
        }

        if (!isValidUrgency(urgency)) {
            throw new RuntimeException(
                    "Invalid urgency. Allowed values: ROUTINE, URGENT, STAT"
            );
        }

        RadiologyOrder order = new RadiologyOrder();

        order.setPatient(patient);
        order.setOrderingDoctor(doctor);
        order.setModality(modality);
        order.setClinicalIndication(
                request.getClinicalIndication().trim()
        );
        order.setClinicalHistory(
                request.getClinicalHistory() == null
                        ? null
                        : request.getClinicalHistory().trim()
        );
        order.setUrgency(urgency);
        order.setStatus("ORDERED");

        RadiologyOrder savedOrder =
                radiologyOrderRepository.save(order);

        return RadiologyOrderResponse.fromEntity(savedOrder);
    }

    @Transactional
    public RadiologyOrderResponse updateStatus(
            Long orderId,
            String newStatus
    ) {

        RadiologyOrder order = radiologyOrderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        String normalizedStatus = newStatus.trim().toUpperCase();

        if (!isValidStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid status. Allowed values: " +
                    "ORDERED, SCHEDULED, ARRIVED, IN_PROGRESS, " +
                    "COMPLETED, CANCELLED"
            );
        }

        String currentStatus = order.getStatus();

        if (!isValidTransition(currentStatus, normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid status transition from "
                            + currentStatus
                            + " to "
                            + normalizedStatus
            );
        }

        LocalDateTime now = LocalDateTime.now();

        switch (normalizedStatus) {

            case "SCHEDULED" -> {
                order.setScheduledAt(now);
                order.setStatus("SCHEDULED");
            }

            case "ARRIVED" -> {
                order.setArrivedAt(now);
                order.setStatus("ARRIVED");
            }

            case "IN_PROGRESS" -> {
                order.setStudyStartedAt(now);
                order.setStatus("IN_PROGRESS");
            }

            case "COMPLETED" -> {
                order.setStatus("COMPLETED");
            }

            case "CANCELLED" -> {
                order.setStatus("CANCELLED");
            }

            default -> order.setStatus(normalizedStatus);
        }

        RadiologyOrder savedOrder =
                radiologyOrderRepository.save(order);

        return RadiologyOrderResponse.fromEntity(savedOrder);
    }

    @Transactional(readOnly = true)
    public RadiologyOrderResponse getOrderById(
            Long id,
            User authenticatedUser
    ) {

        RadiologyOrder order = radiologyOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Radiology order not found"));

        validatePatientAccess(
                order.getPatient(),
                authenticatedUser
        );

        return RadiologyOrderResponse.fromEntity(order);
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getPatientOrders(
            Long patientId,
            User authenticatedUser
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found"));

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        return radiologyOrderRepository
                .findByPatientOrderByCreatedAtDesc(patient)
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getDoctorOrders(
            Long doctorId
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        return radiologyOrderRepository
                .findByOrderingDoctorOrderByCreatedAtDesc(doctor)
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getOrdersByStatus(
            String status
    ) {

        String normalizedStatus = status.trim().toUpperCase();

        if (!isValidStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid status. Allowed values: " +
                    "ORDERED, SCHEDULED, ARRIVED, IN_PROGRESS, " +
                    "COMPLETED, CANCELLED"
            );
        }

        return radiologyOrderRepository
                .findByStatusOrderByCreatedAtDesc(normalizedStatus)
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getOrdersByModality(
            String modality
    ) {

        String normalizedModality = modality.trim().toUpperCase();

        if (!isValidModality(normalizedModality)) {
            throw new RuntimeException(
                    "Invalid modality. Allowed values: XRAY, CT, MRI, ULTRASOUND"
            );
        }

        return radiologyOrderRepository
                .findByModalityOrderByCreatedAtDesc(normalizedModality)
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getOrdersByUrgency(
            String urgency
    ) {

        String normalizedUrgency = urgency.trim().toUpperCase();

        if (!isValidUrgency(normalizedUrgency)) {
            throw new RuntimeException(
                    "Invalid urgency. Allowed values: ROUTINE, URGENT, STAT"
            );
        }

        return radiologyOrderRepository
                .findByUrgencyOrderByCreatedAtDesc(normalizedUrgency)
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getPatientOrdersByStatus(
            Long patientId,
            String status,
            User authenticatedUser
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found"));

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        String normalizedStatus = status.trim().toUpperCase();

        if (!isValidStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid radiology order status"
            );
        }

        return radiologyOrderRepository
                .findByPatientAndStatusOrderByCreatedAtDesc(
                        patient,
                        normalizedStatus
                )
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RadiologyOrderResponse> getDoctorOrdersByStatus(
            Long doctorId,
            String status
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        String normalizedStatus = status.trim().toUpperCase();

        if (!isValidStatus(normalizedStatus)) {
            throw new RuntimeException(
                    "Invalid radiology order status"
            );
        }

        return radiologyOrderRepository
                .findByOrderingDoctorAndStatusOrderByCreatedAtDesc(
                        doctor,
                        normalizedStatus
                )
                .stream()
                .map(RadiologyOrderResponse::fromEntity)
                .toList();
    }

    /**
     * PATIENT users can access only their own patient records.
     * ADMIN, DOCTOR and NURSE access is controlled by
     * @PreAuthorize in the controller.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }

    private boolean isValidModality(String modality) {

        return switch (modality) {
            case "XRAY", "CT", "MRI", "ULTRASOUND" -> true;
            default -> false;
        };
    }

    private boolean isValidUrgency(String urgency) {

        return switch (urgency) {
            case "ROUTINE", "URGENT", "STAT" -> true;
            default -> false;
        };
    }

    private boolean isValidStatus(String status) {

        return switch (status) {
            case "ORDERED",
                 "SCHEDULED",
                 "ARRIVED",
                 "IN_PROGRESS",
                 "COMPLETED",
                 "CANCELLED" -> true;

            default -> false;
        };
    }

    private boolean isValidTransition(
            String currentStatus,
            String newStatus
    ) {

        return switch (currentStatus) {

            case "ORDERED" ->
                    newStatus.equals("SCHEDULED")
                            || newStatus.equals("CANCELLED");

            case "SCHEDULED" ->
                    newStatus.equals("ARRIVED")
                            || newStatus.equals("CANCELLED");

            case "ARRIVED" ->
                    newStatus.equals("IN_PROGRESS")
                            || newStatus.equals("CANCELLED");

            case "IN_PROGRESS" ->
                    newStatus.equals("COMPLETED");

            case "COMPLETED",
                 "CANCELLED" ->
                    false;

            default ->
                    false;
        };
    }
}