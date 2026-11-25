-- ============================================================
-- ROLES
-- ============================================================

INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Full system administration access'),
    ('DOCTOR', 'Clinical access for doctors'),
    ('NURSE', 'Clinical and nursing care access'),
    ('LAB_TECHNICIAN', 'Laboratory operations and result management'),
    ('RADIOLOGY_TECHNICIAN', 'Imaging workflow operations and study capture'),
    ('RADIOLOGIST', 'Radiology interpretation and report authorization'),
    ('PHARMACIST', 'Pharmacy and medication management'),
    ('PATIENT', 'Patient self-service access');


-- ============================================================
-- PERMISSIONS
-- ============================================================

INSERT INTO permissions (code, name, description)
VALUES

    -- User Management
    ('USER_READ', 'View Users', 'View user accounts'),
    ('USER_CREATE', 'Create Users', 'Create user accounts'),
    ('USER_UPDATE', 'Update Users', 'Update user accounts'),
    ('USER_DELETE', 'Delete Users', 'Delete or deactivate user accounts'),

    -- Patient Management
    ('PATIENT_READ', 'View Patients', 'View patient information'),
    ('PATIENT_CREATE', 'Create Patients', 'Register new patients'),
    ('PATIENT_UPDATE', 'Update Patients', 'Update patient information'),
    ('PATIENT_DELETE', 'Delete Patients', 'Deactivate patient records'),

    -- Appointments
    ('APPOINTMENT_READ', 'View Appointments', 'View appointments'),
    ('APPOINTMENT_CREATE', 'Create Appointments', 'Create appointments'),
    ('APPOINTMENT_UPDATE', 'Update Appointments', 'Update appointments'),
    ('APPOINTMENT_CANCEL', 'Cancel Appointments', 'Cancel appointments'),

    -- Clinical Records
    ('CLINICAL_RECORD_READ', 'View Clinical Records', 'View clinical records'),
    ('CLINICAL_RECORD_CREATE', 'Create Clinical Records', 'Create clinical records'),
    ('CLINICAL_RECORD_UPDATE', 'Update Clinical Records', 'Update clinical records'),

    -- Vitals
    ('VITALS_READ', 'View Vitals', 'View patient vitals'),
    ('VITALS_CREATE', 'Record Vitals', 'Record patient vitals'),
    ('VITALS_UPDATE', 'Update Vitals', 'Update patient vitals'),

    -- Prescriptions
    ('PRESCRIPTION_READ', 'View Prescriptions', 'View prescriptions'),
    ('PRESCRIPTION_CREATE', 'Create Prescriptions', 'Create prescriptions'),
    ('PRESCRIPTION_UPDATE', 'Update Prescriptions', 'Update prescriptions'),

    -- Pharmacy
    ('PHARMACY_READ', 'View Pharmacy', 'View pharmacy information'),
    ('PHARMACY_DISPENSE', 'Dispense Medication', 'Dispense prescribed medication'),
    ('PHARMACY_INVENTORY', 'Manage Pharmacy Inventory', 'Manage pharmacy inventory'),

    -- Laboratory
    ('LAB_ORDER_READ', 'View Lab Orders', 'View laboratory orders'),
    ('LAB_ORDER_CREATE', 'Create Lab Orders', 'Create laboratory orders'),
    ('LAB_RESULT_READ', 'View Lab Results', 'View laboratory results'),
    ('LAB_RESULT_CREATE', 'Enter Lab Results', 'Enter laboratory results'),
    ('LAB_RESULT_VERIFY', 'Verify Lab Results', 'Verify laboratory results'),

    -- Inpatient
    ('ADMISSION_READ', 'View Admissions', 'View inpatient admissions'),
    ('ADMISSION_CREATE', 'Create Admissions', 'Admit patients'),
    ('ADMISSION_UPDATE', 'Update Admissions', 'Update admission information'),

    -- Notifications
    ('NOTIFICATION_READ', 'View Notifications', 'View notifications'),
    ('NOTIFICATION_CREATE', 'Create Notifications', 'Create notifications'),

    -- Analytics
    ('ANALYTICS_READ', 'View Analytics', 'View analytics and reports'),

    -- Audit
    ('AUDIT_READ', 'View Audit Logs', 'View system audit logs'),

    -- System
    ('SYSTEM_CONFIG', 'Manage System Configuration', 'Manage system configuration');
    

-- ============================================================
-- ADMIN PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN';


-- ============================================================
-- DOCTOR PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'PATIENT_UPDATE',
        'APPOINTMENT_READ',
        'APPOINTMENT_CREATE',
        'APPOINTMENT_UPDATE',
        'APPOINTMENT_CANCEL',
        'CLINICAL_RECORD_READ',
        'CLINICAL_RECORD_CREATE',
        'CLINICAL_RECORD_UPDATE',
        'VITALS_READ',
        'VITALS_CREATE',
        'VITALS_UPDATE',
        'PRESCRIPTION_READ',
        'PRESCRIPTION_CREATE',
        'PRESCRIPTION_UPDATE',
        'LAB_ORDER_READ',
        'LAB_ORDER_CREATE',
        'LAB_RESULT_READ',
        'ADMISSION_READ',
        'ADMISSION_CREATE',
        'ADMISSION_UPDATE',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'DOCTOR';


-- ============================================================
-- NURSE PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'PATIENT_UPDATE',
        'APPOINTMENT_READ',
        'CLINICAL_RECORD_READ',
        'CLINICAL_RECORD_CREATE',
        'VITALS_READ',
        'VITALS_CREATE',
        'VITALS_UPDATE',
        'PRESCRIPTION_READ',
        'LAB_RESULT_READ',
        'ADMISSION_READ',
        'ADMISSION_CREATE',
        'ADMISSION_UPDATE',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'NURSE';


-- ============================================================
-- LAB TECHNICIAN PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'LAB_ORDER_READ',
        'LAB_RESULT_READ',
        'LAB_RESULT_CREATE',
        'LAB_RESULT_VERIFY',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'LAB_TECHNICIAN';


-- ============================================================
-- RADIOLOGY TECHNICIAN PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'LAB_ORDER_READ',
        'LAB_RESULT_READ',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'RADIOLOGY_TECHNICIAN';


-- ============================================================
-- RADIOLOGIST PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'CLINICAL_RECORD_READ',
        'CLINICAL_RECORD_UPDATE',
        'LAB_RESULT_READ',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'RADIOLOGIST';


-- ============================================================
-- PHARMACIST PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'PRESCRIPTION_READ',
        'PHARMACY_READ',
        'PHARMACY_DISPENSE',
        'PHARMACY_INVENTORY',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'PHARMACIST';


-- ============================================================
-- PATIENT PERMISSIONS
-- ============================================================

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
    ON p.code IN (
        'PATIENT_READ',
        'APPOINTMENT_READ',
        'APPOINTMENT_CREATE',
        'APPOINTMENT_CANCEL',
        'CLINICAL_RECORD_READ',
        'VITALS_READ',
        'PRESCRIPTION_READ',
        'LAB_RESULT_READ',
        'ADMISSION_READ',
        'NOTIFICATION_READ'
    )
WHERE r.name = 'PATIENT';