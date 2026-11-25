CREATE TABLE otp_verifications (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,

    email VARCHAR(255) NOT NULL,

    otp_hash VARCHAR(255) NOT NULL,

    purpose VARCHAR(30) NOT NULL,

    expires_at TIMESTAMP NOT NULL,

    attempt_count INTEGER NOT NULL DEFAULT 0,

    max_attempts INTEGER NOT NULL DEFAULT 5,

    verified_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_otp_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_otp_purpose
        CHECK (
            purpose IN (
                'REGISTRATION',
                'PASSWORD_RESET',
                'EMAIL_CHANGE',
                'LOGIN_MFA'
            )
        )
);

CREATE INDEX idx_otp_user_purpose
    ON otp_verifications(user_id, purpose);

CREATE INDEX idx_otp_email
    ON otp_verifications(email);

CREATE INDEX idx_otp_expires_at
    ON otp_verifications(expires_at);