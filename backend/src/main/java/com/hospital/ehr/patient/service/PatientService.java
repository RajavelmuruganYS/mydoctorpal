package com.hospital.ehr.patient.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.entity.PatientDoctorAssignment;
import com.hospital.ehr.patient.repository.PatientDoctorAssignmentRepository;
import com.hospital.ehr.patient.repository.PatientRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
        private final PatientDoctorAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public PatientService(
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService,
            PatientDoctorAssignmentRepository assignmentRepository
    ) {
        this.patientRepository = patientRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    /**
     * Get the patient record belonging to the
     * currently authenticated user.
     */
    @Transactional(readOnly = true)
    public Patient getPatientByUserId(Long userId) {

        return patientRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient record not found for user ID: " + userId
                        )
                );
    }

    /**
     * Get patient by patient ID.
     */
    @Transactional(readOnly = true)
    public Patient getPatientById(Long patientId) {

        return patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient not found with ID: " + patientId
                        )
                );
    }

        @Transactional(readOnly = true)
        public Patient getPatientById(Long patientId, User authenticatedUser) {
                Patient patient = getPatientById(patientId);
                patientAccessService.assertCanAccess(patient, authenticatedUser);
                return patient;
        }

    /**
     * Get patient by hospital patient number.
     */
    @Transactional(readOnly = true)
    public Patient getPatientByNumber(String patientNumber) {

        return patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient not found with number: "
                                        + patientNumber
                        )
                );
    }

        @Transactional(readOnly = true)
        public Patient getPatientByNumber(String patientNumber, User authenticatedUser) {
                Patient patient = getPatientByNumber(patientNumber);
                patientAccessService.assertCanAccess(patient, authenticatedUser);
                return patient;
        }

    /**
     * Get all patients.
     */
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {

        return patientRepository.findAll();
    }

        @Transactional(readOnly = true)
        public List<Patient> getAssignedPatients(User doctor) {
                return assignmentRepository
                                .findByDoctorAndStatusOrderByAssignedAtDesc(doctor, "ACTIVE")
                                .stream()
                                .map(PatientDoctorAssignment::getPatient)
                                .toList();
        }

    /**
     * Create a patient record for an existing user.
     *
     * The User account must already exist.
     */
    public Patient createPatient(
            Long userId,
            String patientNumber,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContactName,
            String emergencyContactPhone,
            String medicalHistory,
            String allergies
    ) {

        if (patientRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException(
                    "A patient record already exists for this user."
            );
        }

        if (patientRepository.existsByPatientNumber(patientNumber)) {
            throw new IllegalArgumentException(
                    "Patient number already exists: " + patientNumber
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with ID: " + userId
                        )
                );

        Patient patient = new Patient();

        patient.setPatientNumber(patientNumber);
        patient.setUser(user);
        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setAddress(address);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactPhone(emergencyContactPhone);
        patient.setMedicalHistory(medicalHistory);
        patient.setAllergies(allergies);

        return patientRepository.save(patient);
    }

    /**
     * Update an existing patient record using optimistic locking.
     *
     * The version supplied by the client must match the current
     * database version. This prevents a stale client from
     * overwriting a newer patient record.
     */
    public Patient updatePatient(
            Long patientId,
            LocalDate dateOfBirth,
            String gender,
            String bloodGroup,
            String address,
            String emergencyContactName,
            String emergencyContactPhone,
            String medicalHistory,
            String allergies,
            Long expectedVersion
    ) {

        Patient patient = getPatientById(patientId);

        if (!patient.getVersion().equals(expectedVersion)) {
            throw new IllegalStateException(
                    "Patient record has been modified by another user. "
                            + "Please refresh the patient record and try again."
            );
        }

        patient.setDateOfBirth(dateOfBirth);
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setAddress(address);
        patient.setEmergencyContactName(emergencyContactName);
        patient.setEmergencyContactPhone(emergencyContactPhone);
        patient.setMedicalHistory(medicalHistory);
        patient.setAllergies(allergies);

        /*
         * Hibernate will automatically increment the @Version field
         * when this entity is updated.
         */
        return patientRepository.save(patient);
    }

    /**
     * Delete a patient record.
     */
    public void deletePatient(Long patientId) {

        Patient patient = getPatientById(patientId);

        patientRepository.delete(patient);
    }
}