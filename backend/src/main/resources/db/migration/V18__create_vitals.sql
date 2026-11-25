CREATE TABLE vitals (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,
    recorded_by_user_id BIGINT NOT NULL,

    temperature DECIMAL(5,2),
    bp_systolic INTEGER,
    bp_diastolic INTEGER,
    pulse_rate INTEGER,
    spo2 DECIMAL(5,2),
    respiratory_rate INTEGER,

    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vitals_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_vitals_recorded_by_user
        FOREIGN KEY (recorded_by_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_vitals_temperature
        CHECK (temperature IS NULL OR temperature >= 0),

    CONSTRAINT chk_vitals_bp_systolic
        CHECK (bp_systolic IS NULL OR bp_systolic >= 0),

    CONSTRAINT chk_vitals_bp_diastolic
        CHECK (bp_diastolic IS NULL OR bp_diastolic >= 0),

    CONSTRAINT chk_vitals_pulse_rate
        CHECK (pulse_rate IS NULL OR pulse_rate >= 0),

    CONSTRAINT chk_vitals_spo2
        CHECK (spo2 IS NULL OR (spo2 >= 0 AND spo2 <= 100)),

    CONSTRAINT chk_vitals_respiratory_rate
        CHECK (respiratory_rate IS NULL OR respiratory_rate >= 0)
);

CREATE INDEX idx_vitals_patient
    ON vitals(patient_id);

CREATE INDEX idx_vitals_recorded_by_user
    ON vitals(recorded_by_user_id);

CREATE INDEX idx_vitals_recorded_at
    ON vitals(recorded_at);