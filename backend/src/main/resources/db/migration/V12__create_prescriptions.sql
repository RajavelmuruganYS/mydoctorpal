CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,

    doctor_id BIGINT NOT NULL,

    diagnosis_code VARCHAR(20) NOT NULL,

    prescription_date DATE NOT NULL,

    doctor_signature_hash VARCHAR(500) NOT NULL,

    dispense_status VARCHAR(30) NOT NULL DEFAULT 'NOT_DISPENSED',

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    expires_at DATE NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_prescriptions_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id),

    CONSTRAINT fk_prescriptions_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
);

CREATE INDEX idx_prescriptions_patient_id
    ON prescriptions(patient_id);

CREATE INDEX idx_prescriptions_doctor_id
    ON prescriptions(doctor_id);

CREATE INDEX idx_prescriptions_date
    ON prescriptions(prescription_date);

CREATE INDEX idx_prescriptions_status
    ON prescriptions(status);

CREATE INDEX idx_prescriptions_dispense_status
    ON prescriptions(dispense_status);