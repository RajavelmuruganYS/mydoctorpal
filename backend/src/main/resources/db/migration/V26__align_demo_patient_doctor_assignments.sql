-- Align the known local demo accounts with the approved assignment policy.
-- Clinical records are preserved; only assignment status is corrected.
UPDATE patient_doctor_assignments a
SET status = 'REVOKED',
    ended_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
FROM patients p
JOIN users pu ON pu.id = p.user_id
JOIN users du ON TRUE
WHERE a.patient_id = p.id
  AND du.id = a.doctor_id
  AND pu.email = 'patient@hospital.local'
  AND du.email = 'doctor2@hospital.local'
  AND a.status = 'ACTIVE';

UPDATE patient_doctor_assignments a
SET status = 'REVOKED',
    ended_at = CURRENT_TIMESTAMP,
    updated_at = CURRENT_TIMESTAMP
FROM patients p
JOIN users pu ON pu.id = p.user_id
JOIN users du ON TRUE
WHERE a.patient_id = p.id
  AND du.id = a.doctor_id
  AND pu.email = 'ysrajavelmurugan@gmail.com'
  AND du.email = 'doctor@hospital.local'
  AND a.status = 'ACTIVE';

INSERT INTO patient_doctor_assignments (patient_id, doctor_id, status)
SELECT p.id, d.id, 'ACTIVE'
FROM patients p
JOIN users pu ON pu.id = p.user_id
JOIN users d ON d.email = 'doctor@hospital.local'
WHERE pu.email = 'patient@hospital.local'
ON CONFLICT (patient_id, doctor_id)
DO UPDATE SET status = 'ACTIVE', ended_at = NULL, updated_at = CURRENT_TIMESTAMP;

INSERT INTO patient_doctor_assignments (patient_id, doctor_id, status)
SELECT p.id, d.id, 'ACTIVE'
FROM patients p
JOIN users pu ON pu.id = p.user_id
JOIN users d ON d.email = 'doctor2@hospital.local'
WHERE pu.email = 'ysrajavelmurugan@gmail.com'
ON CONFLICT (patient_id, doctor_id)
DO UPDATE SET status = 'ACTIVE', ended_at = NULL, updated_at = CURRENT_TIMESTAMP;
