CREATE TABLE radiology_reports (
    id BIGSERIAL PRIMARY KEY,

    radiology_order_id BIGINT NOT NULL,

    reported_by_user_id BIGINT NOT NULL,

    report_text TEXT NOT NULL,

    findings TEXT,

    impression TEXT,

    critical_finding BOOLEAN NOT NULL DEFAULT FALSE,

    critical_finding_notes TEXT,

    compared_with_prior_study BOOLEAN NOT NULL DEFAULT FALSE,

    prior_study_reference TEXT,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    signed_at TIMESTAMP,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_radiology_reports_order
        FOREIGN KEY (radiology_order_id)
        REFERENCES radiology_orders(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_radiology_reports_user
        FOREIGN KEY (reported_by_user_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_radiology_report_status
        CHECK (
            status IN (
                'DRAFT',
                'FINAL',
                'AMENDED',
                'CANCELLED'
            )
        )
);

CREATE INDEX idx_radiology_reports_order
    ON radiology_reports(radiology_order_id);

CREATE INDEX idx_radiology_reports_reported_by
    ON radiology_reports(reported_by_user_id);

CREATE INDEX idx_radiology_reports_status
    ON radiology_reports(status);

CREATE INDEX idx_radiology_reports_created_at
    ON radiology_reports(created_at);

CREATE INDEX idx_radiology_reports_critical_finding
    ON radiology_reports(critical_finding);