package com.hospital.ehr.auth.service;

import com.hospital.ehr.auth.entity.OtpVerification;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.OtpVerificationRepository;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.integration.email.EmailService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class OtpService {

    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public OtpService(
            OtpVerificationRepository otpRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {

        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate and send registration OTP.
     */
    @Transactional
    public void generateAndSendRegistrationOtp(
            User user
    ) {

        String otp = generateOtp();

        otpRepository.invalidatePreviousOtps(
                user.getId(),
                "REGISTRATION"
        );

        OtpVerification verification =
                new OtpVerification();

        verification.setUser(user);
        verification.setEmail(user.getEmail());
        verification.setOtpHash(
                passwordEncoder.encode(otp)
        );
        verification.setPurpose("REGISTRATION");
        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(OTP_EXPIRY_MINUTES)
        );
        verification.setAttemptCount(0);
        verification.setMaxAttempts(MAX_ATTEMPTS);

        otpRepository.save(verification);

        emailService.sendRegistrationOtp(
                user.getEmail(),
                user.getFirstName(),
                otp
        );
    }

    /**
     * Verify registration OTP.
     */
    @Transactional
    public boolean verifyRegistrationOtp(
            User user,
            String otp
    ) {

        OtpVerification verification =
                otpRepository.findLatestValidOtp(
                        user.getId(),
                        "REGISTRATION"
                );

        if (verification == null) {
            return false;
        }

        if (verification.getVerifiedAt() != null) {
            return false;
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {
            return false;
        }

        if (verification.getAttemptCount()
                >= verification.getMaxAttempts()) {
            return false;
        }

        verification.setAttemptCount(
                verification.getAttemptCount() + 1
        );

        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash()
        )) {

            otpRepository.save(verification);

            return false;
        }

        verification.setVerifiedAt(
                LocalDateTime.now()
        );

        otpRepository.save(verification);

        return true;
    }

    /**
     * Verify registration OTP using email.
     */
    @Transactional
    public void verifyRegistrationOtp(
            String email,
            String otp
    ) {

        User user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        OtpVerification verification =
                otpRepository
                        .findTopByUserIdAndPurposeOrderByCreatedAtDesc(
                                user.getId(),
                                "REGISTRATION"
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "OTP not found. Please request a new OTP."
                                )
                        );

        if (verification.getVerifiedAt() != null) {
            throw new IllegalArgumentException(
                    "OTP has already been used."
            );
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "OTP has expired. Please request a new OTP."
            );
        }

        if (verification.getAttemptCount()
                >= verification.getMaxAttempts()) {

            throw new IllegalArgumentException(
                    "Maximum OTP attempts exceeded. Please request a new OTP."
            );
        }

        verification.setAttemptCount(
                verification.getAttemptCount() + 1
        );

        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash()
        )) {

            otpRepository.save(verification);

            throw new IllegalArgumentException(
                    "Invalid OTP."
            );
        }

        verification.setVerifiedAt(
                LocalDateTime.now()
        );

        otpRepository.save(verification);

        user.setEmailVerified(true);
        user.setAccountStatus("ACTIVE");

        userRepository.save(user);
    }

    /**
     * Generate and send password reset OTP.
     */
    @Transactional
    public void generateAndSendPasswordResetOtp(
            User user
    ) {

        String otp = generateOtp();

        otpRepository.invalidatePreviousOtps(
                user.getId(),
                "PASSWORD_RESET"
        );

        OtpVerification verification =
                new OtpVerification();

        verification.setUser(user);
        verification.setEmail(user.getEmail());

        verification.setOtpHash(
                passwordEncoder.encode(otp)
        );

        verification.setPurpose("PASSWORD_RESET");

        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(OTP_EXPIRY_MINUTES)
        );

        verification.setAttemptCount(0);
        verification.setMaxAttempts(MAX_ATTEMPTS);

        otpRepository.save(verification);

        emailService.sendPasswordResetOtp(
                user.getEmail(),
                user.getFirstName(),
                otp
        );
    }

    /**
     * Verify password reset OTP.
     */
    @Transactional
    public void verifyPasswordResetOtp(
            String email,
            String otp
    ) {

        User user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found."
                        )
                );

        OtpVerification verification =
                otpRepository
                        .findTopByUserIdAndPurposeOrderByCreatedAtDesc(
                                user.getId(),
                                "PASSWORD_RESET"
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Reset OTP not found. Please request a new OTP."
                                )
                        );

        validateOtp(
                verification,
                otp,
                "Reset OTP"
        );
    }

    /**
     * Validate an OTP.
     */
    private void validateOtp(
            OtpVerification verification,
            String otp,
            String otpName
    ) {

        if (verification.getVerifiedAt() != null) {

            throw new IllegalArgumentException(
                    otpName + " has already been used."
            );
        }

        if (verification.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    otpName + " has expired. Please request a new OTP."
            );
        }

        if (verification.getAttemptCount()
                >= verification.getMaxAttempts()) {

            throw new IllegalArgumentException(
                    "Maximum OTP attempts exceeded. Please request a new OTP."
            );
        }

        verification.setAttemptCount(
                verification.getAttemptCount() + 1
        );

        if (!passwordEncoder.matches(
                otp,
                verification.getOtpHash()
        )) {

            otpRepository.save(verification);

            throw new IllegalArgumentException(
                    "Invalid " + otpName + "."
            );
        }

        verification.setVerifiedAt(
                LocalDateTime.now()
        );

        otpRepository.save(verification);
    }

    /**
     * Generate a secure 6-digit OTP.
     */
    private String generateOtp() {

        int number =
                secureRandom.nextInt(900000) + 100000;

        return String.valueOf(number);
    }
}