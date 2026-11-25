package com.hospital.ehr.integration.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailProvider implements EmailProvider {

    private static final Logger log =
            LoggerFactory.getLogger(SmtpEmailProvider.class);

    private final JavaMailSender mailSender;

    /**
     * LOCAL DEVELOPMENT ONLY.
     *
     * When true and the SMTP server cannot be reached / rejects the
     * login, the email (which contains the OTP) is written to the
     * server log instead of failing the request. This lets you
     * register and reset passwords without a working mail account.
     *
     * Set app.mail.log-on-failure=false (MAIL_LOG_ON_FAILURE=false)
     * in any shared or production environment.
     */
    private final boolean logOnFailure;

    public SmtpEmailProvider(
            JavaMailSender mailSender,
            @Value("${app.mail.log-on-failure:false}") boolean logOnFailure
    ) {
        this.mailSender = mailSender;
        this.logOnFailure = logOnFailure;
    }

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            if (!logOnFailure) {
                throw exception;
            }

            log.warn(
                    "SMTP delivery failed ({}). DEV FALLBACK - email that would have been sent:\n"
                            + "  To: {}\n  Subject: {}\n  Body:\n{}",
                    exception.getMessage(),
                    to,
                    subject,
                    body
            );
        }
    }
}
