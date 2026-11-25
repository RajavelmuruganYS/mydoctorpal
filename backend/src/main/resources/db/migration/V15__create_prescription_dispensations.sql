CREATE TABLE prescription_dispensations (
    id BIGSERIAL PRIMARY KEY,

    prescription_id BIGINT NOT NULL,

    pharmacist_id BIGINT NOT NULL,

    dispensed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    status VARCHAR(30) NOT NULL DEFAULT 'DISPENSED',

    notes TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_dispensations_prescription
        FOREIGN KEY (prescription_id)
        REFERENCES prescriptions(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_dispensations_pharmacist
        FOREIGN KEY (pharmacist_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_dispensations_prescription_id
    ON prescription_dispensations(prescription_id);

CREATE INDEX idx_dispensations_pharmacist_id
    ON prescription_dispensations(pharmacist_id);

CREATE INDEX idx_dispensations_dispensed_at
    ON prescription_dispensations(dispensed_at);

CREATE INDEX idx_dispensations_status
    ON prescription_dispensations(status);