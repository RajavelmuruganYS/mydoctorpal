CREATE TABLE drugs (
    id BIGSERIAL PRIMARY KEY,

    brand_name VARCHAR(200) NOT NULL,

    generic_name VARCHAR(200) NOT NULL,

    dosage_form VARCHAR(100) NOT NULL,

    strength VARCHAR(100) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_drugs_brand_name
    ON drugs(brand_name);

CREATE INDEX idx_drugs_generic_name
    ON drugs(generic_name);

CREATE INDEX idx_drugs_active
    ON drugs(active);