package com.hospital.ehr.appointment.controller;

import com.hospital.ehr.appointment.dto.AppointmentResponse;
import com.hospital.ehr.appointment.dto.CancelAppointmentRequest;
import com.hospital.ehr.appointment.dto.CreateAppointmentRequest;
import com.hospital.ehr.appointment.dto.UpdateAppointmentStatusRequest;
import com.hospital.ehr.appointment.entity.Appointment;
import com.hospital.ehr.appointment.service.AppointmentService;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.service.PatientService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;
    private final PatientService patientService;

    public AppointmentController(
            AppointmentService appointmentService,
            UserRepository userRepository,
            PatientService patientService
    ) {
        this.appointmentService = appointmentService;
        this.userRepository = userRepository;
        this.patientService = patientService;
    }

    // ------------------------------------------------------------------
    // Ownership helpers
    // ------------------------------------------------------------------

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        ("ROLE_" + role).equals(authority.getAuthority()));
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found."));
    }

    /**
     * A user who is only a PATIENT may work with their own
     * appointments only. Staff roles are not restricted here.
     */
    private boolean isPatientOnly(Authentication authentication) {
        return hasRole(authentication, "PATIENT")
                && !hasRole(authentication, "ADMIN")
                && !hasRole(authentication, "DOCTOR")
                && !hasRole(authentication, "NURSE");
    }

    private void assertOwnPatient(Authentication authentication, Long patientId) {
        if (!isPatientOnly(authentication)) {
            return;
        }

        Patient own = patientService.getPatientByUserId(
                currentUser(authentication).getId());

        if (patientId == null || !own.getId().equals(patientId)) {
            throw new SecurityException(
                    "You can only manage your own appointments.");
        }
    }

    // Create appointment
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest request,
            Authentication authentication) {

        assertOwnPatient(authentication, request.getPatientId());

        Appointment appointment = appointmentService.createAppointment(
                request.getPatientId(),
                request.getDoctorId(),
                request.getDepartmentId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                request.getEndTime(),
                request.getReason(),
                request.getNotes()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Appointment created successfully.",
                        "appointment", new AppointmentResponse(appointment)
                )
        );
    }

    // Get all appointments - Admin
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAppointments() {

        List<AppointmentResponse> appointments =
                appointmentService.getAllAppointments()
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Get my appointments (patient: own bookings, doctor: own schedule)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<?> getMyAppointments(
            Authentication authentication) {

        User user = currentUser(authentication);

        List<Appointment> appointments = hasRole(authentication, "DOCTOR")
                ? appointmentService.getDoctorAppointments(user.getId())
                : appointmentService.getPatientAppointments(
                        patientService.getPatientByUserId(user.getId()).getId());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments",
                        appointments.stream()
                                .map(AppointmentResponse::new)
                                .toList()
                )
        );
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'LAB_TECHNICIAN', 'PHARMACIST', 'PATIENT')")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable Long id,
            Authentication authentication) {

        Appointment appointment =
                appointmentService.getAppointmentById(id);

        assertOwnPatient(authentication, appointment.getPatient().getId());

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointment", new AppointmentResponse(appointment)
                )
        );
    }

    // Get appointments for a patient
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<?> getPatientAppointments(
            @PathVariable Long patientId) {

        List<AppointmentResponse> appointments =
                appointmentService.getPatientAppointments(patientId)
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Get appointments for a doctor
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<?> getDoctorAppointments(
            @PathVariable Long doctorId) {

        List<AppointmentResponse> appointments =
                appointmentService.getDoctorAppointments(doctorId)
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Get appointments by date
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<?> getAppointmentsByDate(
            @PathVariable LocalDate date) {

        List<AppointmentResponse> appointments =
                appointmentService.getAppointmentsByDate(date)
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Get patient's appointments on a specific date
    @GetMapping("/patient/{patientId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<?> getPatientAppointmentsByDate(
            @PathVariable Long patientId,
            @PathVariable LocalDate date) {

        List<AppointmentResponse> appointments =
                appointmentService.getPatientAppointmentsByDate(
                                patientId, date)
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Get doctor's appointments on a specific date
    @GetMapping("/doctor/{doctorId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<?> getDoctorAppointmentsByDate(
            @PathVariable Long doctorId,
            @PathVariable LocalDate date) {

        List<AppointmentResponse> appointments =
                appointmentService.getDoctorAppointmentsByDate(
                                doctorId, date)
                        .stream()
                        .map(AppointmentResponse::new)
                        .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "appointments", appointments
                )
        );
    }

    // Update appointment status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequest request) {

        Appointment appointment =
                appointmentService.updateStatus(
                        id,
                        request.getStatus()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Appointment status updated successfully.",
                        "appointment", new AppointmentResponse(appointment)
                )
        );
    }

    // Cancel appointment
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentRequest request,
            Authentication authentication) {

        assertOwnPatient(
                authentication,
                appointmentService.getAppointmentById(id).getPatient().getId());

        Appointment appointment =
                appointmentService.cancelAppointment(
                        id,
                        request.getCancellationReason()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Appointment cancelled successfully.",
                        "appointment", new AppointmentResponse(appointment)
                )
        );
    }

    // Delete appointment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAppointment(
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Appointment deleted successfully."
                )
        );
    }
}