CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT,
    patient_id BIGINT,

    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id VARCHAR(100),

    description TEXT,

    ip_address VARCHAR(100),
    user_agent VARCHAR(1000),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_audit_logs_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_audit_logs_user
    ON audit_logs(user_id);

CREATE INDEX idx_audit_logs_patient
    ON audit_logs(patient_id);

CREATE INDEX idx_audit_logs_action
    ON audit_logs(action);

CREATE INDEX idx_audit_logs_resource_type
    ON audit_logs(resource_type);

CREATE INDEX idx_audit_logs_resource_id
    ON audit_logs(resource_id);

CREATE INDEX idx_audit_logs_created_at
    ON audit_logs(created_at);