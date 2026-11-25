package com.hospital.ehr.pharmacy.service;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.pharmacy.entity.PrescriptionDispensation;
import com.hospital.ehr.pharmacy.repository.PrescriptionDispensationRepository;
import com.hospital.ehr.prescription.entity.Prescription;
import com.hospital.ehr.prescription.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PharmacyService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionDispensationRepository dispensationRepository;
    private final UserRepository userRepository;

    public PharmacyService(
            PrescriptionRepository prescriptionRepository,
            PrescriptionDispensationRepository dispensationRepository,
            UserRepository userRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.dispensationRepository = dispensationRepository;
        this.userRepository = userRepository;
    }

    /*
     * Dispense a signed prescription.
     *
     * Any authenticated PHARMACIST is allowed to perform
     * the pharmacy dispensing workflow.
     */
    @Transactional
    public PrescriptionDispensation dispensePrescription(
            Long prescriptionId,
            Long pharmacistId,
            String notes) {

        Prescription prescription =
                prescriptionRepository.findById(prescriptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prescription not found."
                                ));

        User pharmacist =
                userRepository.findById(pharmacistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pharmacist not found."
                                ));

        boolean isPharmacist = pharmacist.getRoles()
                .stream()
                .anyMatch(role ->
                        "PHARMACIST".equals(role.getName()));

        if (!isPharmacist) {
            throw new IllegalArgumentException(
                    "Selected user is not registered as a pharmacist."
            );
        }

        if (!"SIGNED".equals(prescription.getStatus())) {
            throw new IllegalArgumentException(
                    "Only signed prescriptions can be dispensed."
            );
        }

        if (prescription.getExpiresAt() == null
                || prescription.getExpiresAt()
                        .isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Prescription has expired."
            );
        }

        if ("DISPENSED".equals(
                prescription.getDispenseStatus())) {

            throw new IllegalArgumentException(
                    "Prescription has already been dispensed."
            );
        }

        PrescriptionDispensation dispensation =
                new PrescriptionDispensation();

        dispensation.setPrescription(prescription);
        dispensation.setPharmacist(pharmacist);
        dispensation.setStatus("DISPENSED");
        dispensation.setNotes(
                notes == null || notes.isBlank()
                        ? null
                        : notes.trim()
        );

        PrescriptionDispensation savedDispensation =
                dispensationRepository.save(dispensation);

        prescription.setDispenseStatus("DISPENSED");
        prescriptionRepository.save(prescription);

        return savedDispensation;
    }

    /*
     * Get a dispensation by ID.
     *
     * ADMIN:
     *   Can access any pharmacy record.
     *
     * PHARMACIST:
     *   Can access records they dispensed.
     *
     * DOCTOR:
     *   Can access a record only when they are the doctor
     *   who issued the associated prescription.
     */
    @Transactional(readOnly = true)
    public PrescriptionDispensation getDispensationById(
            Long id,
            User authenticatedUser) {

        PrescriptionDispensation dispensation =
                dispensationRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Dispensation record not found."
                                ));

        validateDispensationAccess(
                dispensation,
                authenticatedUser
        );

        return dispensation;
    }

    /*
     * Get all dispensations for a prescription.
     *
     * ADMIN:
     *   Can access any prescription.
     *
     * PHARMACIST:
     *   Can access pharmacy dispensing information.
     *
     * DOCTOR:
     *   Can access only prescriptions they issued.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDispensation> getPrescriptionDispensations(
            Long prescriptionId,
            User authenticatedUser) {

        Prescription prescription =
                prescriptionRepository.findById(prescriptionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Prescription not found."
                                ));

        validatePrescriptionAccess(
                prescription,
                authenticatedUser
        );

        return dispensationRepository
                .findByPrescriptionOrderByDispensedAtDesc(
                        prescription
                );
    }

    /*
     * Get dispensing history for a pharmacist.
     *
     * ADMIN:
     *   Can view any pharmacist's history.
     *
     * PHARMACIST:
     *   Can view only their own history.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDispensation> getPharmacistDispensations(
            Long pharmacistId,
            User authenticatedUser) {

        User pharmacist =
                userRepository.findById(pharmacistId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Pharmacist not found."
                                ));

        validatePharmacistAccess(
                pharmacist,
                authenticatedUser
        );

        return dispensationRepository
                .findByPharmacistOrderByDispensedAtDesc(
                        pharmacist
                );
    }

    /*
     * Get dispensations by status.
     *
     * This is a pharmacy operational endpoint.
     * ADMIN and PHARMACIST access is controlled by the
     * controller.
     */
    @Transactional(readOnly = true)
    public List<PrescriptionDispensation> getDispensationsByStatus(
            String status) {

        return dispensationRepository
                .findByStatusOrderByDispensedAtDesc(status);
    }

    /*
     * Validate access to an individual dispensation.
     */
    private void validateDispensationAccess(
            PrescriptionDispensation dispensation,
            User authenticatedUser) {

        if (dispensation == null
                || authenticatedUser == null) {

            throw new SecurityException(
                    "You are not authorized to access this dispensing record."
            );
        }

        if (hasRole(authenticatedUser, "ADMIN")) {
            return;
        }

        if (hasRole(authenticatedUser, "PHARMACIST")) {

            User pharmacist =
                    dispensation.getPharmacist();

            if (pharmacist == null
                    || pharmacist.getId() == null
                    || !pharmacist.getId()
                            .equals(authenticatedUser.getId())) {

                throw new SecurityException(
                        "You are not authorized to access this dispensing record."
                );
            }

            return;
        }

        if (hasRole(authenticatedUser, "DOCTOR")) {

            Prescription prescription =
                    dispensation.getPrescription();

            if (prescription == null
                    || prescription.getDoctor() == null
                    || prescription.getDoctor().getId() == null
                    || !prescription.getDoctor().getId()
                            .equals(authenticatedUser.getId())) {

                throw new SecurityException(
                        "You are not authorized to access this dispensing record."
                );
            }

            return;
        }

        throw new SecurityException(
                "You are not authorized to access this dispensing record."
        );
    }

    /*
     * Validate access to a prescription's dispensing history.
     */
    private void validatePrescriptionAccess(
            Prescription prescription,
            User authenticatedUser) {

        if (prescription == null
                || authenticatedUser == null) {

            throw new SecurityException(
                    "You are not authorized to access this prescription's dispensing records."
            );
        }

        if (hasRole(authenticatedUser, "ADMIN")) {
            return;
        }

        /*
         * Pharmacists are part of the pharmacy workflow and
         * can access dispensing information for prescriptions.
         */
        if (hasRole(authenticatedUser, "PHARMACIST")) {
            return;
        }

        /*
         * Doctors can access dispensing information only
         * for prescriptions they issued.
         */
        if (hasRole(authenticatedUser, "DOCTOR")) {

            if (prescription.getDoctor() != null
                    && prescription.getDoctor().getId() != null
                    && prescription.getDoctor().getId()
                            .equals(authenticatedUser.getId())) {

                return;
            }
        }

        throw new SecurityException(
                "You are not authorized to access this prescription's dispensing records."
        );
    }

    /*
     * Validate pharmacist-specific history access.
     */
    private void validatePharmacistAccess(
            User pharmacist,
            User authenticatedUser) {

        if (pharmacist == null
                || authenticatedUser == null) {

            throw new SecurityException(
                    "You are not authorized to access this pharmacist's dispensing records."
            );
        }

        if (hasRole(authenticatedUser, "ADMIN")) {
            return;
        }

        if (hasRole(authenticatedUser, "PHARMACIST")
                && pharmacist.getId() != null
                && pharmacist.getId()
                        .equals(authenticatedUser.getId())) {

            return;
        }

        throw new SecurityException(
                "You are not authorized to access this pharmacist's dispensing records."
        );
    }

    private boolean hasRole(
            User user,
            String roleName) {

        return user.getRoles()
                .stream()
                .anyMatch(role ->
                        roleName.equals(role.getName()));
    }
}