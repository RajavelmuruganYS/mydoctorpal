# Hospital EHR — mydoctorpal (Spring Boot API + React frontend)

One repository, two apps, one PostgreSQL database.

```
backend/         Spring Boot API (com.hospital.ehr) on http://localhost:8080/api/v1
frontend/        React + Vite + Tailwind app on http://localhost:5173
database/        demo-seed.sql  (demo users, departments, drugs, one patient record)
docker-compose.yml   PostgreSQL 16 for local development
.env             local settings read by the backend (dev-only values)
```

The frontend talks to the real backend for everything — there is no mock data anywhere in `frontend/src`
(the only test doubles are in `frontend/src/test/` and are used by `npm test`, never by the app).

---

## 1. Prerequisites

| Tool | Version |
|---|---|
| Java (JDK) | 21 (`java.version` in `backend/pom.xml`) |
| Node.js | 20 or newer (with npm) |
| Docker Desktop | any recent version — *or* a local PostgreSQL 14+ |

You do **not** need to install Maven; `backend/mvnw` (`mvnw.cmd` on Windows) is included.

## 2. Start the database

```bash
docker compose up -d db          # from the repository root
docker compose ps                # STATUS should say "healthy"
```

This creates database `hospital_ehr` (user `postgres`, password `123456`) — exactly what
`backend/src/main/resources/application.yaml` expects. Using your own PostgreSQL instead?
Create an empty `hospital_ehr` database and set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in `.env`.

## 3. Start the backend

```bash
cd backend
./mvnw spring-boot:run           # Windows: mvnw.cmd spring-boot:run
```

* First start: Flyway creates all tables and the six roles (migrations `V1`–`V24`). Wait for
  `Started ... in ... seconds`.
* If the first build fails, run `./mvnw -DskipTests compile` and read the first error — see *Known limits* below.
* Swagger UI: <http://localhost:8080/swagger-ui.html> (if enabled in your build).
* The backend reads `../.env`. It already contains a freshly generated `JWT_SECRET`.
  Email is optional in development: while `MAIL_LOG_ON_FAILURE=true` and no SMTP account is
  configured, **the OTP e-mail text (with the 6-digit code) is printed in the backend console**.

## 4. Load the demo data (once)

Only after the backend has started once (so the tables exist):

```bash
docker exec -i ehr-postgres psql -U postgres -d hospital_ehr < database/demo-seed.sql
```

(Windows PowerShell: `Get-Content database/demo-seed.sql | docker exec -i ehr-postgres psql -U postgres -d hospital_ehr`.)
It is safe to run repeatedly.

| Role | Email | Password |
|---|---|---|
| Admin | `admin@hospital.local` | `Admin@12345` |
| Doctor | `doctor@hospital.local` | `Doctor@12345` |
| Doctor | `doctor2@hospital.local` | `Doctor@12345` |
| Nurse | `nurse@hospital.local` | `Nurse@12345` |
| Lab technician | `lab@hospital.local` | `Lab@123456` |
| Pharmacist | `pharmacist@hospital.local` | `Pharma@12345` |
| Patient (has a patient record, `PAT-2026-000001`) | `patient@hospital.local` | `Patient@12345` |

These are **demo passwords — never load this file in production.**

## 5. Start the frontend

```bash
cd frontend
npm install
npm run dev                      # http://localhost:5173
```

`frontend/.env` points at the API (`VITE_API_BASE_URL=http://localhost:8080/api/v1`). Change it if the backend runs elsewhere,
and add the frontend's origin to `CORS_ALLOWED_ORIGINS` in the root `.env`.

## 6. Walk through it (a good end-to-end check)

1. **Patient:** sign in as `patient@hospital.local` → *Appointments → Book appointment* (pick Dr. Carter + a department, a future date, times).
2. **Doctor:** sign in as `doctor@hospital.local` → *Dashboard* shows the appointment. Open the patient → **Prescriptions → New prescription** (drug list comes from the catalogue) → open it → **Sign**.
   Also try **Order a test** (Laboratory), **Record vitals**, **Order imaging**, **Admit a patient**.
3. **Pharmacist:** *Pharmacy* → look the patient up by number `PAT-2026-000001` → **Dispense** the signed prescription.
4. **Lab technician:** *Laboratory* → open the order → **Record a result**. The patient (and doctor) now see it.
5. **Nurse:** ward overview, vitals, admissions (discharge), imaging status changes.
6. **Admin:** *Users*, *Audit logs*, *System overview*; **Create patient record** for newly registered patients.
7. **Registration flow:** *Create account* → a 6-digit code is emailed (or printed in the backend console) → verify → sign in.
   A newly registered patient sees “Your patient record is not set up yet” until an admin creates the record (*Users → Create record*).

## 7. What was fixed (summary)

**Frontend**
* All four auth flows (verify email, forgot / verify / reset password) were broken by `authApi.verify - email(form)`-style code and never passed the e-mail / OTP between pages. Rewritten.
* API layer now understands both response styles the backend uses (`{success, appointments:[…]}` and raw arrays/objects). Lists were empty before.
* Token refresh: login failures no longer trigger a refresh/redirect; expired tokens are refreshed once and the request is retried.
* Drug search used the wrong parameter names (`brandName`/`genericName`; backend expects `name`).
* Real pages for every module with real actions: sign / dispense prescriptions, book / cancel / update appointments, record lab results, admit / discharge, vitals + charts, radiology orders / studies / reports (with the backend's status-transition rules), notifications, admin users & audit logs.
* No more typing raw IDs: patient, doctor, department and drug pickers; patients get their own `patientId` automatically.
* Role dashboards are built from live endpoints (the backend's non-admin dashboard endpoints only return static stubs).
* Replaced placeholder pages (`EditPatient`, `DispensePrescription`) and deleted ~35 unused mock-style components.
* Pinned dependency versions (were all `latest`), a real ESLint config (the old one had no rules), lazy-loaded routes (1.05 MB → 0.3 MB main bundle).

**Backend** (business logic untouched)
* `SecurityConfig`: unauthenticated / expired-token requests now return JSON **401** (they were 403, so refresh could never trigger); forbidden → JSON 403.
* `GlobalExceptionHandler`: validation errors (with per-field messages), not-found (404), ownership violations (403), optimistic-lock and constraint conflicts (409). Before, most business errors surfaced as an empty 500.
* `GET /appointments/me` (patients could not list their own appointments); patients can now only create / read / cancel **their own** appointments.
* `GET /directory/doctors`, `GET /directory/departments` (needed to book an appointment).
* Patient responses now include name, email and phone (`PatientResponse`).
* Dev OTP fallback (`MAIL_LOG_ON_FAILURE`) so registration works without a mail server; DB and CORS settings overridable by environment.
* `database/demo-seed.sql` and `docker-compose.yml` (there were none).

## 8. Verification that was actually run

* `npm run lint` and `npm run build` — clean.
* `npm test` — 97 tests: every screen for every role is rendered against responses shaped exactly like the Spring controllers; the auth flows, token-refresh and the write payloads (appointment booking, prescription with items, drug search) are asserted.
* All 24 Flyway migrations plus `demo-seed.sql` were executed (twice) against a real PostgreSQL 16.
* **Not run:** the Spring Boot application itself. The environment used to prepare this had no access to Maven Central, so the Java changes were syntax-checked only. Run `./mvnw -DskipTests compile` first; if anything fails to compile, it will be in one of the files listed under *Backend* above.

## 9. Known limits / notes

* Secrets: rotate any mail password / JWT secret that was ever stored in a shared `.env`. The `.env` in this repo holds development-only values.
* A user holding several roles is shown the dashboard and menu of the highest-privilege role (Admin > Doctor > Nurse > Lab > Pharmacist > Patient). The backend still checks every request.
* The backend returns raw IDs for who ordered / dispensed something; the UI resolves doctor and patient names where the current role is allowed to list them (lab / pharmacy staff see `Patient #id`).
* Refresh tokens are kept in `localStorage` (as in the original design). For a hardened production deployment consider HttpOnly cookies.
