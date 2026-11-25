package com.hospital.ehr.appointment.entity;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "appointments",
        indexes = {
                @Index(
                        name = "idx_appointments_patient_id",
                        columnList = "patient_id"
                ),
                @Index(
                        name = "idx_appointments_doctor_id",
                        columnList = "doctor_id"
                ),
                @Index(
                        name = "idx_appointments_date",
                        columnList = "appointment_date"
                ),
                @Index(
                        name = "idx_appointments_status",
                        columnList = "status"
                )
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Patient who booked/owns the appointment.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_appointments_patient"
            )
    )
    private Patient patient;

    /**
     * Doctor assigned to the appointment.
     *
     * Doctor is represented by the User entity
     * with role DOCTOR.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_appointments_doctor"
            )
    )
    private User doctor;

    /**
     * Department in which the appointment takes place.
     */
    @Column(name = "department_id")
    private Long departmentId;

    /**
     * Appointment date.
     */
    @Column(
            name = "appointment_date",
            nullable = false
    )
    private LocalDate appointmentDate;

    /**
     * Appointment starting time.
     */
    @Column(
            name = "start_time",
            nullable = false
    )
    private LocalTime startTime;

    /**
     * Appointment ending time.
     */
    @Column(
            name = "end_time",
            nullable = false
    )
    private LocalTime endTime;

    /**
     * Reason for consultation.
     */
    @Column(
            name = "reason",
            length = 500
    )
    private String reason;

    /**
     * Additional appointment notes.
     */
    @Column(
            name = "notes",
            columnDefinition = "TEXT"
    )
    private String notes;

    /**
     * Appointment status.
     *
     * Examples:
     * SCHEDULED
     * CONFIRMED
     * COMPLETED
     * CANCELLED
     * NO_SHOW
     */
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private String status;

    /**
     * Reason for cancellation.
     */
    @Column(
            name = "cancellation_reason",
            columnDefinition = "TEXT"
    )
    private String cancellationReason;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public Appointment() {
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null || status.isBlank()) {
            status = "SCHEDULED";
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }

    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getDoctor() {
        return doctor;
    }

    public void setDoctor(User doctor) {
        this.doctor = doctor;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}