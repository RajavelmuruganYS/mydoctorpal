package com.hospital.ehr.integration.email;

public interface EmailProvider {

    void sendEmail(
            String to,
            String subject,
            String body
    );
}