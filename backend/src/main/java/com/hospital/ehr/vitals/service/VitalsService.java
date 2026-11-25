package com.hospital.ehr.vitals.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.vitals.dto.CreateVitalRequest;
import com.hospital.ehr.vitals.dto.VitalResponse;
import com.hospital.ehr.vitals.entity.Vital;
import com.hospital.ehr.vitals.repository.VitalRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VitalsService {

    private final VitalRepository vitalRepository;
    private final PatientRepository patientRepository;
        private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public VitalsService(
            VitalRepository vitalRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.vitalRepository = vitalRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public VitalResponse recordVitals(
            CreateVitalRequest request,
            Long recordedByUserId
    ) {

        User recordedByUser = userRepository.findById(recordedByUserId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Patient patient = patientRepository.findById(
                request.getPatientId()
        ).orElseThrow(() ->
                new RuntimeException("Patient not found")
        );

        patientAccessService.assertCanAccess(patient, recordedByUser);

        Vital vital = new Vital();

        vital.setPatient(patient);
        vital.setRecordedByUser(recordedByUser);

        vital.setTemperature(request.getTemperature());
        vital.setBpSystolic(request.getBpSystolic());
        vital.setBpDiastolic(request.getBpDiastolic());
        vital.setPulseRate(request.getPulseRate());
        vital.setSpo2(request.getSpo2());
        vital.setRespiratoryRate(request.getRespiratoryRate());

        Vital savedVital = vitalRepository.save(vital);

        return new VitalResponse(savedVital);
    }

    @Transactional(readOnly = true)
    public VitalResponse getVitalById(
            Long vitalId,
            User authenticatedUser
    ) {

        Vital vital = vitalRepository.findById(vitalId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Vital record not found"
                        )
                );

        validatePatientAccess(
                vital.getPatient(),
                authenticatedUser
        );

        return new VitalResponse(vital);
    }

    @Transactional(readOnly = true)
    public List<VitalResponse> getPatientVitals(
            Long patientId,
            User authenticatedUser
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found")
                );

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        return vitalRepository
                .findByPatientOrderByRecordedAtDesc(patient)
                .stream()
                .map(VitalResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public VitalResponse getLatestPatientVitals(
            Long patientId,
            User authenticatedUser
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new RuntimeException("Patient not found")
                );

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        Vital vital = vitalRepository
                .findFirstByPatientOrderByRecordedAtDesc(patient)
                .orElseThrow(() ->
                        new RuntimeException(
                                "No vital records found for patient"
                        )
                );

        return new VitalResponse(vital);
    }

    @Transactional(readOnly = true)
    public List<VitalResponse> getVitalsRecordedByUser(
            Long userId
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return vitalRepository
                .findByRecordedByUserOrderByRecordedAtDesc(user)
                .stream()
                .map(VitalResponse::new)
                .toList();
    }

    /**
     * Verifies whether the authenticated user is allowed
     * to access the requested patient's clinical data.
     *
     * ADMIN, DOCTOR and NURSE can access patient records.
     *
     * PATIENT users can access only their own patient record.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }
}