CREATE TABLE prescription_items (
    id BIGSERIAL PRIMARY KEY,

    prescription_id BIGINT NOT NULL,
    drug_id BIGINT NOT NULL,

    dosage_form VARCHAR(100) NOT NULL,
    strength VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,

    duration_days INTEGER NOT NULL,

    special_instructions TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_prescription_items_prescription
        FOREIGN KEY (prescription_id)
        REFERENCES prescriptions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_prescription_items_drug
        FOREIGN KEY (drug_id)
        REFERENCES drugs(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_prescription_items_duration
        CHECK (duration_days > 0)
);

CREATE INDEX idx_prescription_items_prescription_id
    ON prescription_items(prescription_id);

CREATE INDEX idx_prescription_items_drug_id
    ON prescription_items(drug_id);