package com.hospital.ehr.appointment.service;

import com.hospital.ehr.appointment.entity.Appointment;
import com.hospital.ehr.appointment.repository.AppointmentRepository;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.service.PatientService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PatientService patientService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            UserRepository userRepository,
            PatientService patientService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.patientService = patientService;
    }

    // =========================================================
    // CREATE APPOINTMENT
    // =========================================================

    /**
     * Create a new appointment.
     *
     * The patient and doctor are loaded from the database.
     * The service also checks whether the doctor already
     * has an overlapping appointment.
     */
    public Appointment createAppointment(
            Long patientId,
            Long doctorId,
            Long departmentId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            String reason,
            String notes
    ) {

        // -----------------------------------------------------
        // Validate date and time
        // -----------------------------------------------------

        if (appointmentDate == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Appointment start time and end time are required."
            );
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Appointment end time must be after start time."
            );
        }

        // -----------------------------------------------------
        // Prevent appointments in the past
        // -----------------------------------------------------

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if (appointmentDate.isBefore(today)) {
            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        if (
                appointmentDate.equals(today)
                        && !startTime.isAfter(currentTime)
        ) {
            throw new IllegalArgumentException(
                    "Appointment start time must be in the future."
            );
        }

        // -----------------------------------------------------
        // Find patient
        // -----------------------------------------------------

        Patient patient = patientService.getPatientById(patientId);

        // -----------------------------------------------------
        // Find doctor
        // -----------------------------------------------------

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Doctor not found."
                        )
                );

        // -----------------------------------------------------
        // Verify that selected user is actually a doctor
        // -----------------------------------------------------

        boolean isDoctor = doctor.getRoles()
                .stream()
                .anyMatch(
                        role -> "DOCTOR".equalsIgnoreCase(
                                role.getName()
                        )
                );

        if (!isDoctor) {
            throw new IllegalArgumentException(
                    "Selected user is not a doctor."
            );
        }

        // -----------------------------------------------------
        // Check doctor appointment conflict
        // -----------------------------------------------------

        boolean conflict =
                appointmentRepository
                        .existsByDoctorAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctor,
                                appointmentDate,
                                endTime,
                                startTime
                        );

        if (conflict) {
            throw new IllegalArgumentException(
                    "Doctor already has an appointment during the selected time."
            );
        }

        // -----------------------------------------------------
        // Create appointment
        // -----------------------------------------------------

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setDepartmentId(departmentId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setReason(reason);
        appointment.setNotes(notes);
        appointment.setStatus("SCHEDULED");

        return appointmentRepository.save(appointment);
    }

    // =========================================================
    // GET APPOINTMENT BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Appointment not found."
                        )
                );
    }

    // =========================================================
    // GET ALL APPOINTMENTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    // =========================================================
    // GET PATIENT APPOINTMENTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getPatientAppointments(
            Long patientId
    ) {

        Patient patient =
                patientService.getPatientById(patientId);

        return appointmentRepository
                .findByPatientOrderByAppointmentDateAscStartTimeAsc(
                        patient
                );
    }

    // =========================================================
    // GET DOCTOR APPOINTMENTS
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getDoctorAppointments(
            Long doctorId
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Doctor not found."
                        )
                );

        return appointmentRepository
                .findByDoctorOrderByAppointmentDateAscStartTimeAsc(
                        doctor
                );
    }

    // =========================================================
    // GET APPOINTMENTS BY DATE
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsByDate(
            LocalDate date
    ) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        return appointmentRepository
                .findByAppointmentDateOrderByStartTimeAsc(
                        date
                );
    }

    // =========================================================
    // GET PATIENT APPOINTMENTS BY DATE
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getPatientAppointmentsByDate(
            Long patientId,
            LocalDate date
    ) {

        Patient patient =
                patientService.getPatientById(patientId);

        return appointmentRepository
                .findByPatientAndAppointmentDateOrderByStartTimeAsc(
                        patient,
                        date
                );
    }

    // =========================================================
    // GET DOCTOR APPOINTMENTS BY DATE
    // =========================================================

    @Transactional(readOnly = true)
    public List<Appointment> getDoctorAppointmentsByDate(
            Long doctorId,
            LocalDate date
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Doctor not found."
                        )
                );

        return appointmentRepository
                .findByDoctorAndAppointmentDateOrderByStartTimeAsc(
                        doctor,
                        date
                );
    }

    // =========================================================
    // UPDATE APPOINTMENT STATUS
    // =========================================================

    public Appointment updateStatus(
            Long id,
            String status
    ) {

        Appointment appointment =
                getAppointmentById(id);

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Appointment status is required."
            );
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        switch (normalizedStatus) {

            case "SCHEDULED":
            case "CONFIRMED":
            case "COMPLETED":
            case "CANCELLED":
            case "NO_SHOW":
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid appointment status."
                );
        }

        appointment.setStatus(normalizedStatus);

        return appointmentRepository.save(appointment);
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    public Appointment cancelAppointment(
            Long id,
            String cancellationReason
    ) {

        Appointment appointment =
                getAppointmentById(id);

        if ("COMPLETED".equalsIgnoreCase(
                appointment.getStatus()
        )) {
            throw new IllegalArgumentException(
                    "Completed appointments cannot be cancelled."
            );
        }

        if ("CANCELLED".equalsIgnoreCase(
                appointment.getStatus()
        )) {
            throw new IllegalArgumentException(
                    "Appointment is already cancelled."
            );
        }

        appointment.setStatus("CANCELLED");

        appointment.setCancellationReason(
                cancellationReason
        );

        return appointmentRepository.save(appointment);
    }

    // =========================================================
    // DELETE APPOINTMENT
    // =========================================================

    public void deleteAppointment(Long id) {

        Appointment appointment =
                getAppointmentById(id);

        appointmentRepository.delete(appointment);
    }
}