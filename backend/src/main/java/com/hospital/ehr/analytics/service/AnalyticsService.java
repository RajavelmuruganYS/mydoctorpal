package com.hospital.ehr.analytics.service;

import com.hospital.ehr.appointment.repository.AppointmentRepository;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.inpatient.repository.AdmissionRepository;
import com.hospital.ehr.laboratory.repository.LabOrderRepository;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.prescription.repository.PrescriptionRepository;
import com.hospital.ehr.radiology.repository.RadiologyOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AdmissionRepository admissionRepository;
    private final LabOrderRepository labOrderRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RadiologyOrderRepository radiologyOrderRepository;

    public AnalyticsService(
            UserRepository userRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            AdmissionRepository admissionRepository,
            LabOrderRepository labOrderRepository,
            PrescriptionRepository prescriptionRepository,
            RadiologyOrderRepository radiologyOrderRepository) {

        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.admissionRepository = admissionRepository;
        this.labOrderRepository = labOrderRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.radiologyOrderRepository = radiologyOrderRepository;
    }

    public Map<String, Object> getAdminDashboardStatistics() {

        LocalDate today = LocalDate.now();

        long totalUsers = userRepository.count();

        long totalPatients = patientRepository.count();

        long todayAppointments =
                appointmentRepository
                        .findByAppointmentDateOrderByStartTimeAsc(today)
                        .size();

        long activeAdmissions =
                admissionRepository
                        .findByStatusOrderByAdmissionDateDesc("ADMITTED")
                        .size();

        long pendingLabOrders =
                labOrderRepository
                        .findByStatusOrderByOrderedAtAsc("ORDERED")
                        .size();

        long pendingPrescriptions =
                prescriptionRepository
                        .findByDispenseStatusOrderByPrescriptionDateAsc(
                                "NOT_DISPENSED"
                        )
                        .size();

        long pendingRadiologyOrders =
                radiologyOrderRepository
                        .findByStatusOrderByCreatedAtDesc("ORDERED")
                        .size();

        Map<String, Object> statistics = new LinkedHashMap<>();

        statistics.put("totalUsers", totalUsers);
        statistics.put("totalPatients", totalPatients);
        statistics.put("todayAppointments", todayAppointments);
        statistics.put("activeAdmissions", activeAdmissions);
        statistics.put("pendingLabOrders", pendingLabOrders);
        statistics.put("pendingPrescriptions", pendingPrescriptions);
        statistics.put("pendingRadiologyOrders", pendingRadiologyOrders);

        return statistics;
    }
}