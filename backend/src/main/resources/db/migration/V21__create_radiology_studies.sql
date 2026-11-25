CREATE TABLE radiology_studies (
    id BIGSERIAL PRIMARY KEY,

    radiology_order_id BIGINT NOT NULL,

    accession_number VARCHAR(100) NOT NULL UNIQUE,

    study_instance_uid VARCHAR(255) UNIQUE,

    series_count INTEGER,

    image_count INTEGER,

    storage_type VARCHAR(30) NOT NULL DEFAULT 'PACS',

    storage_reference VARCHAR(1000),

    viewer_url VARCHAR(2000),

    study_status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',

    performed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_radiology_studies_order
        FOREIGN KEY (radiology_order_id)
        REFERENCES radiology_orders(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_radiology_studies_storage_type
        CHECK (
            storage_type IN (
                'PACS',
                'DICOM_SERVER',
                'LOCAL',
                'EXTERNAL'
            )
        ),

    CONSTRAINT chk_radiology_studies_status
        CHECK (
            study_status IN (
                'PENDING',
                'AVAILABLE',
                'ARCHIVED',
                'FAILED'
            )
        ),

    CONSTRAINT chk_radiology_studies_series_count
        CHECK (
            series_count IS NULL OR series_count >= 0
        ),

    CONSTRAINT chk_radiology_studies_image_count
        CHECK (
            image_count IS NULL OR image_count >= 0
        )
);

CREATE INDEX idx_radiology_studies_order
    ON radiology_studies(radiology_order_id);

CREATE INDEX idx_radiology_studies_accession
    ON radiology_studies(accession_number);

CREATE INDEX idx_radiology_studies_status
    ON radiology_studies(study_status);

CREATE INDEX idx_radiology_studies_performed_at
    ON radiology_studies(performed_at);