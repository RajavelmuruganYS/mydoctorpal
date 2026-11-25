package com.hospital.ehr.integration.email;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    /**
     * Registration OTP email.
     */
    public String registrationOtpEmail(
            String firstName,
            String otp
    ) {

        return """
                Hello %s,

                Welcome to Hospital Health Record System.

                Your email verification OTP is:

                %s

                This OTP will expire in 10 minutes.

                If you did not create this account, please ignore this email.

                Regards,
                Hospital Health Record System
                """.formatted(firstName, otp);
    }

    /**
     * Password reset OTP email.
     */
    public String passwordResetOtpEmail(
            String firstName,
            String otp
    ) {

        return """
                Hello %s,

                We received a request to reset your password.

                Your password reset OTP is:

                %s

                This OTP will expire in 10 minutes.

                If you did not request a password reset, please ignore this email.

                Regards,
                Hospital Health Record System
                """.formatted(firstName, otp);
    }
}