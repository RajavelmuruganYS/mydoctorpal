package com.hospital.ehr.integration.email;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final EmailProvider emailProvider;
    private final EmailTemplateService templateService;

    public EmailService(
            EmailProvider emailProvider,
            EmailTemplateService templateService
    ) {
        this.emailProvider = emailProvider;
        this.templateService = templateService;
    }

    /**
     * Send registration OTP.
     */
    public void sendRegistrationOtp(
            String email,
            String firstName,
            String otp
    ) {

        String subject =
                "Hospital EHR - Email Verification OTP";

        String body =
                templateService.registrationOtpEmail(
                        firstName,
                        otp
                );

        emailProvider.sendEmail(
                email,
                subject,
                body
        );
    }

    /**
     * Send password reset OTP.
     */
    public void sendPasswordResetOtp(
            String email,
            String firstName,
            String otp
    ) {

        String subject =
                "Hospital EHR - Password Reset OTP";

        String body =
                templateService.passwordResetOtpEmail(
                        firstName,
                        otp
                );

        emailProvider.sendEmail(
                email,
                subject,
                body
        );
    }
}