package com.hospital.ehr.inpatient.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.inpatient.dto.AdmissionResponse;
import com.hospital.ehr.inpatient.dto.CreateAdmissionRequest;
import com.hospital.ehr.inpatient.entity.Admission;
import com.hospital.ehr.inpatient.repository.AdmissionRepository;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InpatientService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
        private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public InpatientService(
            AdmissionRepository admissionRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public AdmissionResponse admitPatient(
            CreateAdmissionRequest request,
            Long doctorId
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found")
                );

        boolean isDoctor = doctor.getRoles()
                .stream()
                .anyMatch(role ->
                        "DOCTOR".equalsIgnoreCase(role.getName())
                );

        if (!isDoctor) {
            throw new RuntimeException(
                    "Only a doctor can admit a patient"
            );
        }

        Patient patient = patientRepository.findById(
                request.getPatientId()
        ).orElseThrow(() ->
                new RuntimeException("Patient not found")
        );

        patientAccessService.assertDoctorCanAccess(patient, doctor);

        boolean alreadyAdmitted =
                admissionRepository.existsByPatientAndStatus(
                        patient,
                        "ADMITTED"
                );

        if (alreadyAdmitted) {
            throw new RuntimeException(
                    "Patient already has an active admission"
            );
        }

        String category = request.getAdmissionCategory();

        if (category == null || category.isBlank()) {
            category = "GENERAL";
        }

        category = category.trim().toUpperCase();

        if (!category.equals("GENERAL")
                && !category.equals("EMERGENCY")
                && !category.equals("SURGERY")
                && !category.equals("ICU")
                && !category.equals("MATERNITY")
                && !category.equals("PEDIATRIC")) {

            throw new RuntimeException(
                    "Invalid admission category. Allowed values: " +
                    "GENERAL, EMERGENCY, SURGERY, ICU, MATERNITY, PEDIATRIC"
            );
        }

        Admission admission = new Admission();

        admission.setPatient(patient);
        admission.setAdmittingDoctor(doctor);
        admission.setBedNumber(request.getBedNumber());
        admission.setAdmissionCategory(category);
        admission.setDiagnosisOnAdmission(
                request.getDiagnosisOnAdmission()
        );
        admission.setAdmissionDate(LocalDateTime.now());
        admission.setStatus("ADMITTED");

        Admission savedAdmission =
                admissionRepository.save(admission);

        return new AdmissionResponse(savedAdmission);
    }

    @Transactional
    public AdmissionResponse dischargePatient(
            Long admissionId
    ) {

        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() ->
                        new RuntimeException("Admission not found")
                );

        if ("DISCHARGED".equalsIgnoreCase(admission.getStatus())) {
            throw new RuntimeException(
                    "Patient is already discharged"
            );
        }

        if ("CANCELLED".equalsIgnoreCase(admission.getStatus())) {
            throw new RuntimeException(
                    "Cancelled admission cannot be discharged"
            );
        }

        admission.setStatus("DISCHARGED");
        admission.setDischargeDate(LocalDateTime.now());

        Admission updatedAdmission =
                admissionRepository.save(admission);

        return new AdmissionResponse(updatedAdmission);
    }

    @Transactional(readOnly = true)
    public AdmissionResponse getAdmissionById(
            Long admissionId,
            User authenticatedUser
    ) {

        Admission admission = admissionRepository.findById(admissionId)
                .orElseThrow(() ->
                        new RuntimeException("Admission not found")
                );

        validatePatientAccess(
                admission.getPatient(),
                authenticatedUser
        );

        return new AdmissionResponse(admission);
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> getPatientAdmissions(
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

        return admissionRepository
                .findByPatientOrderByAdmissionDateDesc(patient)
                .stream()
                .map(AdmissionResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> getDoctorAdmissions(
            Long doctorId
    ) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found")
                );

        return admissionRepository
                .findByAdmittingDoctorOrderByAdmissionDateDesc(doctor)
                .stream()
                .map(AdmissionResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> getAdmissionsByStatus(
            String status
    ) {

        return admissionRepository
                .findByStatusOrderByAdmissionDateDesc(
                        status.toUpperCase()
                )
                .stream()
                .map(AdmissionResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdmissionResponse> getAdmissionsByCategory(
            String category
    ) {

        return admissionRepository
                .findByAdmissionCategoryOrderByAdmissionDateDesc(
                        category.toUpperCase()
                )
                .stream()
                .map(AdmissionResponse::new)
                .toList();
    }

    /**
     * Ensures that a PATIENT can access only their own
     * admission records.
     *
     * ADMIN, DOCTOR and NURSE access is controlled by
     * @PreAuthorize in the controller.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }
}