package com.hospital.ehr.appointment.repository;

import com.hospital.ehr.appointment.entity.Appointment;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a particular patient.
     */
    List<Appointment> findByPatientOrderByAppointmentDateAscStartTimeAsc(
            Patient patient
    );

    /**
     * Find all appointments assigned to a particular doctor.
     */
    List<Appointment> findByDoctorOrderByAppointmentDateAscStartTimeAsc(
            User doctor
    );

    /**
     * Find appointments for a patient on a specific date.
     */
    List<Appointment> findByPatientAndAppointmentDateOrderByStartTimeAsc(
            Patient patient,
            LocalDate appointmentDate
    );

    /**
     * Find appointments for a doctor on a specific date.
     */
    List<Appointment> findByDoctorAndAppointmentDateOrderByStartTimeAsc(
            User doctor,
            LocalDate appointmentDate
    );

    /**
     * Find appointments by date.
     */
    List<Appointment> findByAppointmentDateOrderByStartTimeAsc(
            LocalDate appointmentDate
    );

    /**
     * Find appointments by status.
     */
    List<Appointment> findByStatusOrderByAppointmentDateAscStartTimeAsc(
            String status
    );

    /**
     * Check whether a doctor already has an appointment
     * during the requested date and time range.
     */
    boolean existsByDoctorAndAppointmentDateAndStartTimeLessThanAndEndTimeGreaterThan(
            User doctor,
            LocalDate appointmentDate,
            java.time.LocalTime endTime,
            java.time.LocalTime startTime
    );

    /**
     * Find a patient's appointment by ID.
     */
    Optional<Appointment> findByIdAndPatient(
            Long id,
            Patient patient
    );

    /**
     * Find a doctor's appointment by ID.
     */
    Optional<Appointment> findByIdAndDoctor(
            Long id,
            User doctor
    );
}