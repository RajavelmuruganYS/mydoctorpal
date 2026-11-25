CREATE TABLE admissions (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,
    admitting_doctor_id BIGINT NOT NULL,

    bed_number VARCHAR(50),

    admission_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    discharge_date TIMESTAMP,

    admission_category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',

    diagnosis_on_admission TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'ADMITTED',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_admissions_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_admissions_doctor
        FOREIGN KEY (admitting_doctor_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_admissions_patient
    ON admissions(patient_id);

CREATE INDEX idx_admissions_doctor
    ON admissions(admitting_doctor_id);

CREATE INDEX idx_admissions_status
    ON admissions(status);

CREATE INDEX idx_admissions_admission_date
    ON admissions(admission_date);