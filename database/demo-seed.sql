-- ============================================================
-- DEMO SEED DATA  (local development only - do NOT run in production)
-- Run AFTER the backend has started once (Flyway creates the tables
-- and the roles), e.g.:
--   docker exec -i ehr-postgres psql -U postgres -d hospital_ehr < database/demo-seed.sql
-- Safe to run more than once.
--
-- Demo logins (email / password):
--   admin@hospital.local        Admin@12345     ADMIN
--   doctor@hospital.local       Doctor@12345    DOCTOR
--   doctor2@hospital.local      Doctor@12345    DOCTOR
--   nurse@hospital.local        Nurse@12345     NURSE
--   lab@hospital.local          Lab@123456      LAB_TECHNICIAN
--   radtech@hospital.local      Radtech@12345   RADIOLOGY_TECHNICIAN
--   pharmacist@hospital.local   Pharma@12345    PHARMACIST
--   patient@hospital.local      Patient@12345   PATIENT
-- ============================================================

-- Departments (needed for appointment booking)
INSERT INTO departments (department_code, name, description) VALUES
  ('GEN', 'General Medicine', 'Primary care and general consultations'),
  ('CARD', 'Cardiology', 'Heart and vascular care'),
  ('PED', 'Pediatrics', 'Care for infants, children and adolescents'),
  ('ORTH', 'Orthopedics', 'Bones, joints and muscles'),
  ('RAD', 'Radiology', 'Diagnostic imaging')
ON CONFLICT DO NOTHING;

-- Users (all ACTIVE and email-verified so they can log in immediately)
INSERT INTO users (email, password_hash, first_name, last_name, email_verified, account_status, password_changed_at) VALUES
  ('admin@hospital.local', '$2b$10$aR.qvza8S.XGBiUw91peAOKcN.3AP.89ogrM7LTUOHlfGp1CdrCyO', 'Ava', 'Admin', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('doctor@hospital.local', '$2b$10$zP18uK.zptApN3qt4Vb19O4pHvLqVs8yxeZtDMrrgNfbP0/HhwBlS', 'Daniel', 'Carter', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('doctor2@hospital.local', '$2b$10$DwLdaXCGGpopmQRczQgSW.OzC/SgGYFdlatCAvLAmkPF/riclC0ty', 'Priya', 'Nair', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('nurse@hospital.local', '$2b$10$/a1sEDCzvy.KJ4g29Ljnm.cuDOe3YgVfBPNTVpx.zSVJ6IU3O36Za', 'Nora', 'Bennett', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('lab@hospital.local', '$2b$10$Ro4eBHmpp9QpvDPSr7KngOFV5ML1syliVars/m.oJzjArPpdiw3cS', 'Liam', 'Foster', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('radtech@hospital.local', '$2b$10$PVFoDwWWbQPv67Gujuuk2.r3tdZEJKGoVQ0a.MRM1jhuRky0E2Hb6', 'Rita', 'Mason', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('pharmacist@hospital.local', '$2b$10$3M0gsyMY3Pb6tU5LkpsGz.nMGtqgUNa9CRA0XcLxwtVj6N6g9s9C6', 'Paula', 'Reyes', TRUE, 'ACTIVE', CURRENT_TIMESTAMP),
  ('patient@hospital.local', '$2b$10$owECSYLGh1txZKuS.pPfx.Uosblb.sZR3XzEdfbwVyOEhRJ3EZvLy', 'Peter', 'Walker', TRUE, 'ACTIVE', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;

-- Role assignments
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM (VALUES
  ('admin@hospital.local', 'ADMIN'),
  ('doctor@hospital.local', 'DOCTOR'),
  ('doctor2@hospital.local', 'DOCTOR'),
  ('nurse@hospital.local', 'NURSE'),
  ('lab@hospital.local', 'LAB_TECHNICIAN'),
  ('radtech@hospital.local', 'RADIOLOGY_TECHNICIAN'),
  ('pharmacist@hospital.local', 'PHARMACIST'),
  ('patient@hospital.local', 'PATIENT')
) AS v(email, role_name)
JOIN users u ON u.email = v.email
JOIN roles r ON r.name = v.role_name
ON CONFLICT DO NOTHING;

-- Put the first doctor in General Medicine
UPDATE users SET department_id = (SELECT id FROM departments WHERE department_code = 'GEN')
WHERE email = 'doctor@hospital.local' AND department_id IS NULL;
UPDATE users SET department_id = (SELECT id FROM departments WHERE department_code = 'CARD')
WHERE email = 'doctor2@hospital.local' AND department_id IS NULL;

-- Patient record for the demo patient account
INSERT INTO patients (patient_number, user_id, date_of_birth, gender, blood_group, address,
                      emergency_contact_name, emergency_contact_phone, medical_history, allergies)
SELECT 'PAT-2026-000001', u.id, DATE '1990-04-12', 'MALE', 'O+', '12 Green Street, Coimbatore',
       'Mary Walker', '+91 90000 00001', 'Seasonal asthma', 'Penicillin'
FROM users u WHERE u.email = 'patient@hospital.local'
ON CONFLICT DO NOTHING;

-- Drug catalogue
INSERT INTO drugs (brand_name, generic_name, dosage_form, strength)
SELECT * FROM (VALUES
  ('Crocin', 'Paracetamol', 'Tablet', '500 mg'),
  ('Amoxil', 'Amoxicillin', 'Capsule', '500 mg'),
  ('Brufen', 'Ibuprofen', 'Tablet', '400 mg'),
  ('Glycomet', 'Metformin', 'Tablet', '500 mg'),
  ('Ventolin', 'Salbutamol', 'Inhaler', '100 mcg'),
  ('Pantocid', 'Pantoprazole', 'Tablet', '40 mg')
) AS d(brand_name, generic_name, dosage_form, strength)
WHERE NOT EXISTS (SELECT 1 FROM drugs);
