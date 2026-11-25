CREATE TABLE lab_orders (
    id BIGSERIAL PRIMARY KEY,

    patient_id BIGINT NOT NULL,

    doctor_id BIGINT NOT NULL,

    test_name VARCHAR(200) NOT NULL,

    test_code VARCHAR(50),

    priority VARCHAR(30) NOT NULL DEFAULT 'ROUTINE',

    clinical_notes TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'ORDERED',

    ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lab_orders_patient
        FOREIGN KEY (patient_id)
        REFERENCES patients(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_lab_orders_doctor
        FOREIGN KEY (doctor_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_lab_orders_patient_id
    ON lab_orders(patient_id);

CREATE INDEX idx_lab_orders_doctor_id
    ON lab_orders(doctor_id);

CREATE INDEX idx_lab_orders_status
    ON lab_orders(status);

CREATE INDEX idx_lab_orders_ordered_at
    ON lab_orders(ordered_at);


CREATE TABLE lab_results (
    id BIGSERIAL PRIMARY KEY,

    lab_order_id BIGINT NOT NULL,

    lab_technician_id BIGINT NOT NULL,

    result_value TEXT,

    unit VARCHAR(50),

    reference_range VARCHAR(200),

    interpretation TEXT,

    result_status VARCHAR(30) NOT NULL DEFAULT 'PRELIMINARY',

    performed_at TIMESTAMP,

    verified_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_lab_results_order
        FOREIGN KEY (lab_order_id)
        REFERENCES lab_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_lab_results_technician
        FOREIGN KEY (lab_technician_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_lab_results_lab_order_id
    ON lab_results(lab_order_id);

CREATE INDEX idx_lab_results_technician_id
    ON lab_results(lab_technician_id);

CREATE INDEX idx_lab_results_status
    ON lab_results(result_status);

CREATE INDEX idx_lab_results_performed_at
    ON lab_results(performed_at);