package com.hospital.ehr.patient.dto;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * API representation of a patient record.
 *
 * Same fields as the Patient entity plus the linked user's
 * display details (firstName, lastName, email, phone) so that the
 * frontend can show real names instead of bare IDs.
 *
 * The user association is fetched with the patient
 * (see PatientRepository @EntityGraph), so no lazy loading
 * happens while this object is built.
 */
public class PatientResponse {

    private final Long id;
    private final Long userId;
    private final String patientNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final String gender;
    private final String bloodGroup;
    private final String address;
    private final String emergencyContactName;
    private final String emergencyContactPhone;
    private final String medicalHistory;
    private final String allergies;
    private final Long version;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private PatientResponse(Patient patient) {
        User user = patient.getUser();

        this.id = patient.getId();
        this.userId = user != null ? user.getId() : null;
        this.patientNumber = patient.getPatientNumber();
        this.firstName = user != null ? user.getFirstName() : null;
        this.lastName = user != null ? user.getLastName() : null;
        this.email = user != null ? user.getEmail() : null;
        this.phone = user != null ? user.getPhone() : null;
        this.dateOfBirth = patient.getDateOfBirth();
        this.gender = patient.getGender();
        this.bloodGroup = patient.getBloodGroup();
        this.address = patient.getAddress();
        this.emergencyContactName = patient.getEmergencyContactName();
        this.emergencyContactPhone = patient.getEmergencyContactPhone();
        this.medicalHistory = patient.getMedicalHistory();
        this.allergies = patient.getAllergies();
        this.version = patient.getVersion();
        this.createdAt = patient.getCreatedAt();
        this.updatedAt = patient.getUpdatedAt();
    }

    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient);
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getPatientNumber() { return patientNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getBloodGroup() { return bloodGroup; }
    public String getAddress() { return address; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getMedicalHistory() { return medicalHistory; }
    public String getAllergies() { return allergies; }
    public Long getVersion() { return version; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
