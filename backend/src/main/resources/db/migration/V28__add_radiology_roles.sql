-- Add dedicated radiology roles for imaging workflows.

INSERT INTO roles (name, description)
SELECT 'RADIOLOGY_TECHNICIAN', 'Imaging workflow operations and study capture'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'RADIOLOGY_TECHNICIAN');

INSERT INTO roles (name, description)
SELECT 'RADIOLOGIST', 'Radiology interpretation and report authorization'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'RADIOLOGIST');

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
  ON p.code IN ('PATIENT_READ', 'LAB_ORDER_READ', 'LAB_RESULT_READ', 'NOTIFICATION_READ')
WHERE r.name = 'RADIOLOGY_TECHNICIAN'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      JOIN roles rr ON rr.id = rp.role_id
      JOIN permissions pp ON pp.id = rp.permission_id
      WHERE rr.name = 'RADIOLOGY_TECHNICIAN' AND pp.code IN ('PATIENT_READ', 'LAB_ORDER_READ', 'LAB_RESULT_READ', 'NOTIFICATION_READ')
  );

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
  ON p.code IN ('PATIENT_READ', 'CLINICAL_RECORD_READ', 'CLINICAL_RECORD_UPDATE', 'LAB_RESULT_READ', 'NOTIFICATION_READ')
WHERE r.name = 'RADIOLOGIST'
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      JOIN roles rr ON rr.id = rp.role_id
      JOIN permissions pp ON pp.id = rp.permission_id
      WHERE rr.name = 'RADIOLOGIST' AND pp.code IN ('PATIENT_READ', 'CLINICAL_RECORD_READ', 'CLINICAL_RECORD_UPDATE', 'LAB_RESULT_READ', 'NOTIFICATION_READ')
  );