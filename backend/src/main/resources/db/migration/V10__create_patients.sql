CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,

    patient_number VARCHAR(30) NOT NULL UNIQUE,

    user_id BIGINT NOT NULL UNIQUE,

    date_of_birth DATE,

    gender VARCHAR(20),

    blood_group VARCHAR(10),

    address VARCHAR(500),

    emergency_contact_name VARCHAR(100),

    emergency_contact_phone VARCHAR(20),

    medical_history TEXT,

    allergies TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_patients_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);