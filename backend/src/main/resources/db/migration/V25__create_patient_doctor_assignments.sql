CREATE TABLE patient_doctor_assignments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_patient_doctor_assignments_patient
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    CONSTRAINT fk_patient_doctor_assignments_doctor
        FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_patient_doctor_assignments_status
        CHECK (status IN ('ACTIVE', 'REVOKED')),
    CONSTRAINT uk_patient_doctor_assignments_pair
        UNIQUE (patient_id, doctor_id)
);

CREATE INDEX idx_patient_doctor_assignments_patient
    ON patient_doctor_assignments(patient_id);

CREATE INDEX idx_patient_doctor_assignments_doctor_status
    ON patient_doctor_assignments(doctor_id, status);

CREATE OR REPLACE FUNCTION touch_patient_doctor_assignments_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_patient_doctor_assignments_updated_at
    BEFORE UPDATE ON patient_doctor_assignments
    FOR EACH ROW EXECUTE FUNCTION touch_patient_doctor_assignments_updated_at();

-- Preserve existing clinical workflows by assigning doctors already connected
-- to patients through appointments or prescriptions.
INSERT INTO patient_doctor_assignments (patient_id, doctor_id)
SELECT DISTINCT patient_id, doctor_id
FROM (
    SELECT patient_id, doctor_id FROM appointments
    UNION
    SELECT patient_id, doctor_id FROM prescriptions
) existing_relationships
ON CONFLICT (patient_id, doctor_id) DO NOTHING;
