package com.hospital.ehr.prescription.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.patient.entity.Patient;
import com.hospital.ehr.prescription.dto.PrescriptionItemRequest;
import com.hospital.ehr.prescription.entity.Drug;
import com.hospital.ehr.prescription.entity.Prescription;
import com.hospital.ehr.prescription.entity.PrescriptionItem;
import com.hospital.ehr.prescription.repository.DrugRepository;
import com.hospital.ehr.prescription.repository.PrescriptionItemRepository;
import com.hospital.ehr.prescription.repository.PrescriptionRepository;
import com.hospital.ehr.patient.repository.PatientRepository;
import com.hospital.ehr.patient.service.PatientAccessService;
import com.hospital.ehr.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final DrugRepository drugRepository;
    private final PatientRepository patientRepository;
        private final UserRepository userRepository;
        private final PatientAccessService patientAccessService;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            DrugRepository drugRepository,
            PatientRepository patientRepository,
            UserRepository userRepository,
            PatientAccessService patientAccessService
    ) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.drugRepository = drugRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
        this.patientAccessService = patientAccessService;
    }

    @Transactional
    public Prescription createPrescription(
            Long patientId,
            Long doctorId,
            String diagnosisCode,
            List<PrescriptionItemRequest> itemRequests
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Patient not found.")
                );

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Doctor not found.")
                );

        boolean isDoctor = doctor.getRoles()
                .stream()
                .anyMatch(role -> "DOCTOR".equals(role.getName()));

        if (!isDoctor) {
            throw new IllegalArgumentException(
                    "Selected user is not registered as a doctor."
            );
        }

        patientAccessService.assertDoctorCanAccess(patient, doctor);

        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one prescription item is required."
            );
        }

        Prescription prescription = new Prescription();

        prescription.setPatient(patient);
        prescription.setDoctor(doctor);
        prescription.setDiagnosisCode(diagnosisCode);
        prescription.setPrescriptionDate(LocalDate.now());

        /*
         * Temporary hash for the DRAFT workflow.
         * The final SHA-256 signature hash is generated
         * when the doctor signs the prescription.
         */
        prescription.setDoctorSignatureHash(
                "DRAFT-" + doctorId + "-" + System.currentTimeMillis()
        );

        prescription.setStatus("DRAFT");
        prescription.setDispenseStatus("NOT_DISPENSED");
        prescription.setExpiresAt(
                LocalDate.now().plusDays(30)
        );

        Prescription savedPrescription =
                prescriptionRepository.save(prescription);

        List<PrescriptionItem> items = new ArrayList<>();

        for (PrescriptionItemRequest request : itemRequests) {

            Drug drug = drugRepository.findById(request.getDrugId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Drug not found: " + request.getDrugId()
                            )
                    );

            if (!Boolean.TRUE.equals(drug.getActive())) {
                throw new IllegalArgumentException(
                        "Selected drug is inactive: "
                                + drug.getBrandName()
                );
            }

            PrescriptionItem item = new PrescriptionItem();

            item.setPrescription(savedPrescription);
            item.setDrug(drug);
            item.setDosageForm(request.getDosageForm());
            item.setStrength(request.getStrength());
            item.setFrequency(request.getFrequency());
            item.setDurationDays(request.getDurationDays());
            item.setSpecialInstructions(
                    request.getSpecialInstructions()
            );

            items.add(item);
        }

        prescriptionItemRepository.saveAll(items);

        return savedPrescription;
    }

    /*
     * Get a prescription by ID with patient-level authorization.
     *
     * The transaction keeps the Patient -> User relationship
     * available during the ownership check.
     */
    @Transactional(readOnly = true)
    public Prescription getPrescriptionById(
            Long id,
            User authenticatedUser
    ) {

        Prescription prescription =
                prescriptionRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prescription not found."
                                )
                        );

        validatePatientAccess(
                prescription.getPatient(),
                authenticatedUser
        );

        return prescription;
    }

    /*
     * Get all prescriptions for a patient with
     * patient-level authorization.
     */
    @Transactional(readOnly = true)
    public List<Prescription> getPatientPrescriptions(
            Long patientId,
            User authenticatedUser
    ) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Patient not found."
                        )
                );

        validatePatientAccess(
                patient,
                authenticatedUser
        );

        return prescriptionRepository
                .findByPatientOrderByPrescriptionDateDesc(patient);
    }

    /*
     * Get prescriptions created by a doctor.
     */
    @Transactional(readOnly = true)
    public List<Prescription> getDoctorPrescriptions(Long doctorId) {

        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Doctor not found."
                        )
                );

        return prescriptionRepository
                .findByDoctorOrderByPrescriptionDateDesc(doctor);
    }

    /*
     * Get prescription items with patient-level authorization.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionItem> getPrescriptionItems(
            Long prescriptionId,
            User authenticatedUser
    ) {

        Prescription prescription =
                getPrescriptionById(
                        prescriptionId,
                        authenticatedUser
                );

        return prescriptionItemRepository
                .findByPrescriptionOrderByIdAsc(prescription);
    }

    @Transactional
    public Prescription signPrescription(
            Long prescriptionId,
            Long doctorId
    ) {

        Prescription prescription =
                prescriptionRepository.findById(prescriptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prescription not found."
                                )
                        );

        /*
         * Only the doctor who created the prescription
         * can sign it.
         */
        if (prescription.getDoctor() == null
                || !doctorId.equals(
                        prescription.getDoctor().getId()
                )) {

            throw new IllegalArgumentException(
                    "You are not authorized to sign this prescription."
            );
        }

        /*
         * Only DRAFT prescriptions can be signed.
         */
        if (!"DRAFT".equals(prescription.getStatus())) {

            throw new IllegalArgumentException(
                    "Only draft prescriptions can be signed."
            );
        }

        /*
         * Do not allow signing after the prescription
         * validity period has expired.
         */
        if (prescription.getExpiresAt() == null
                || prescription.getExpiresAt()
                        .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Prescription has expired."
            );
        }

        /*
         * Build the data that represents the prescription.
         */
        String signatureData =
                prescription.getId()
                        + "|"
                        + prescription.getDoctor().getId()
                        + "|"
                        + prescription.getPatient().getId()
                        + "|"
                        + prescription.getDiagnosisCode()
                        + "|"
                        + prescription.getPrescriptionDate()
                        + "|"
                        + System.currentTimeMillis();

        /*
         * Generate SHA-256 hash.
         */
        String signatureHash =
                generateSha256(signatureData);

        prescription.setDoctorSignatureHash(signatureHash);

        /*
         * Move prescription from DRAFT to SIGNED.
         */
        prescription.setStatus("SIGNED");

        return prescriptionRepository.save(prescription);
    }

    /*
     * Patient ownership validation.
     *
     * ADMIN, DOCTOR, NURSE and PHARMACIST can access
     * patient records according to their controller roles.
     *
     * PATIENT can access only their own records.
     */
    private void validatePatientAccess(
            Patient patient,
            User authenticatedUser
    ) {
        patientAccessService.assertCanAccess(patient, authenticatedUser);
    }

    private String generateSha256(String data) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            data.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }
}