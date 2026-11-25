CREATE TABLE radiology_study_files (
    id BIGSERIAL PRIMARY KEY,
    study_id BIGINT NOT NULL,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(200),
    size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    uploaded_by_id BIGINT NOT NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_radiology_study_files_study
        FOREIGN KEY (study_id)
        REFERENCES radiology_studies(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_radiology_study_files_uploaded_by
        FOREIGN KEY (uploaded_by_id)
        REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_radiology_study_files_study
    ON radiology_study_files(study_id);

CREATE INDEX idx_radiology_study_files_uploaded_at
    ON radiology_study_files(uploaded_at DESC);
