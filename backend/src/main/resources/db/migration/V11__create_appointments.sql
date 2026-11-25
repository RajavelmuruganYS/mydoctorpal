-- ============================================================
-- V11 - Create Appointments
-- Hospital Health Record Management System
-- ============================================================

CREATE TABLE appointments (

    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,

    doctor_id BIGINT NOT NULL,

    department_id BIGINT,

    appointment_date DATE NOT NULL,

    start_time TIME NOT NULL,

    end_time TIME NOT NULL,

    reason VARCHAR(500),

    notes TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',

    cancellation_reason VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- ========================================================
    -- Foreign Keys
    -- ========================================================

    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_appointments_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
        ON DELETE SET NULL,

    -- ========================================================
    -- Validation
    -- ========================================================

    CONSTRAINT chk_appointment_time
        CHECK (end_time > start_time),

    CONSTRAINT chk_appointment_status
        CHECK (
            status IN (
                'SCHEDULED',
                'CONFIRMED',
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED',
                'NO_SHOW'
            )
        )
);

-- ============================================================
-- Indexes
-- ============================================================

CREATE INDEX idx_appointments_patient
    ON appointments(patient_id);

CREATE INDEX idx_appointments_doctor
    ON appointments(doctor_id);

CREATE INDEX idx_appointments_date
    ON appointments(appointment_date);

CREATE INDEX idx_appointments_status
    ON appointments(status);

CREATE INDEX idx_appointments_doctor_date
    ON appointments(doctor_id, appointment_date);