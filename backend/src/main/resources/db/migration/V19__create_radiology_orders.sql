CREATE TABLE radiology_orders (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,
    ordering_doctor_id BIGINT NOT NULL,

    modality VARCHAR(30) NOT NULL,
    clinical_indication TEXT NOT NULL,
    clinical_history TEXT,

    urgency VARCHAR(30) NOT NULL DEFAULT 'ROUTINE',

    scheduled_at TIMESTAMP,
    arrived_at TIMESTAMP,
    study_started_at TIMESTAMP,

    status VARCHAR(30) NOT NULL DEFAULT 'ORDERED',

    radiation_dose DECIMAL(10,4),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_radiology_orders_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_radiology_orders_doctor
        FOREIGN KEY (ordering_doctor_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_radiology_modality
        CHECK (
            modality IN (
                'XRAY',
                'CT',
                'MRI',
                'ULTRASOUND'
            )
        ),

    CONSTRAINT chk_radiology_urgency
        CHECK (
            urgency IN (
                'ROUTINE',
                'URGENT',
                'STAT'
            )
        ),

    CONSTRAINT chk_radiology_status
        CHECK (
            status IN (
                'ORDERED',
                'SCHEDULED',
                'ARRIVED',
                'IN_PROGRESS',
                'COMPLETED',
                'CANCELLED'
            )
        ),

    CONSTRAINT chk_radiology_radiation_dose
        CHECK (
            radiation_dose IS NULL
            OR radiation_dose >= 0
        )
);

CREATE INDEX idx_radiology_orders_patient
    ON radiology_orders(patient_id);

CREATE INDEX idx_radiology_orders_doctor
    ON radiology_orders(ordering_doctor_id);

CREATE INDEX idx_radiology_orders_status
    ON radiology_orders(status);

CREATE INDEX idx_radiology_orders_modality
    ON radiology_orders(modality);

CREATE INDEX idx_radiology_orders_scheduled_at
    ON radiology_orders(scheduled_at);