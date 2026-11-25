CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100),

    phone VARCHAR(20),

    department_id BIGINT,

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    account_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_EMAIL_VERIFICATION',

    failed_login_attempts INTEGER NOT NULL DEFAULT 0,

    locked_until TIMESTAMP,

    last_login_at TIMESTAMP,

    password_changed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id),

    CONSTRAINT chk_users_account_status
        CHECK (
            account_status IN (
                'PENDING_EMAIL_VERIFICATION',
                'PENDING_APPROVAL',
                'ACTIVE',
                'SUSPENDED',
                'LOCKED',
                'DEACTIVATED'
            )
        )
);