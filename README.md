# Blood Donor Management System — Backend Documentation

## Table of Contents

- [Overview](#overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Security](#security)
- [REST API Reference](#rest-api-reference)
- [Domain Model](#domain-model)
- [Business Workflows](#business-workflows)
- [Event System](#event-system)
- [Notification System](#notification-system)
- [Exception Handling](#exception-handling)
- [Database](#database)
- [Testing](#testing)
- [Deployment](#deployment)

---

## Overview

A comprehensive blood donation management platform built with Spring Boot. The system supports the full donation lifecycle — from donor registration, appointment booking, and medical examination through blood collection, lab testing, and report generation. It implements role-based access control for six user types: Head Administrator (GOD), Admin, Doctor, Nurse, Lab Technician, and Donor.

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.1 |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA / Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Validation | Spring Validation (Jakarta Bean Validation) |
| API Docs | SpringDoc OpenAPI 3.0.0 (Swagger UI) |
| Rate Limiting | Bucket4j 8.16 |
| Build Tool | Gradle |
| Code Gen | Lombok |
| Containerization | Docker, Docker Compose |
| Testing | JUnit 5, Testcontainers (PostgreSQL) |

---

## Architecture

The project follows **Hexagonal Architecture** (Ports & Adapters) with Clean Architecture layering within each bounded context:

```
ifmo.se.coursach_back/
├── admin/            # Admin bounded context
├── appointment/      # Appointment scheduling
├── audit/            # Audit trail
├── auth/             # Authentication & registration
├── config/           # Cross-cutting configuration
├── donor/            # Donor self-service
├── examination/      # Medical examinations
├── exception/        # Global exception handling
├── lab/              # Laboratory workflow
├── medical/          # Doctor workflow
├── notification/     # Notification delivery
├── nurse/            # Nurse workflow
├── report/           # Report generation
├── role/             # Role management
├── security/         # JWT, filters, principals
├── shared/           # Shared domain (Account, Role)
└── staff/            # Staff data access
```

Each bounded context is structured internally with up to four layers:

| Layer | Package | Responsibility |
|-------|---------|---------------|
| **API** | `api/` | REST controllers, request/response DTOs |
| **Application** | `application/` | Use cases (interfaces + service implementations), commands, results, ports (repository interfaces) |
| **Domain** | `domain/` | JPA entities, enums, value objects |
| **Infrastructure** | `infra/` | Repository adapters, JPA repositories, event listeners |

---

## Getting Started

### Prerequisites

- JDK 17+
- Docker & Docker Compose (for PostgreSQL)
- Gradle 8+ (or use the included Gradle wrapper)

### Run with Docker Compose

```bash
docker compose up --build
```

This starts PostgreSQL 16 and the application. The API will be available at `http://localhost:8080`.

### Run locally (development)

1. Start PostgreSQL:
   ```bash
   docker compose up postgres
   ```

2. Build and run the application:
   ```bash
   ./gradlew bootRun
   ```

   The API will be available at `http://localhost:9696`.

### Swagger UI

Once running, access the interactive API documentation:
- **Swagger UI**: `http://localhost:<port>/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:<port>/v3/api-docs`

### Demo Accounts

The application bootstraps demo accounts on startup:

| Email | Password | Role |
|-------|----------|------|
| `god@system.local` | `big_papa` | GOD (Head Admin) |
| `admin@system.local` | `admin_pass` | ADMIN |
| `doctor@system.local` | `doctor_pass` | DOCTOR |
| `nurse@system.local` | `nurse_pass` | NURSE |
| `lab@system.local` | `lab_pass` | LAB |
| `donor@system.local` | `donor_pass` | DONOR |

---

## Configuration

All configuration is in `src/main/resources/application.properties`. Key settings can be overridden via environment variables.

| Property | Env Variable | Default | Description |
|----------|-------------|---------|-------------|
| `spring.datasource.url` | `DB_URL` | `jdbc:postgresql://localhost:5432/blood_donation` | Database JDBC URL |
| `spring.datasource.username` | `DB_USER` | `postgres` | Database user |
| `spring.datasource.password` | `DB_PASSWORD` | `postgres` | Database password |
| `security.jwt.secret` | `JWT_SECRET` / `SECURITY_JWT_SECRET` | — | JWT signing key (≥32 chars, HMAC-SHA) |
| `security.jwt.expiration-minutes` | `JWT_EXP_MIN` | `60` | Token TTL in minutes |
| `cors.allowed-origins` | `CORS_ORIGINS` | `http://localhost:3000,http://localhost:5173,http://localhost:27843` | Allowed CORS origins |
| `server.port` | — | `9696` | Server listening port |
| `spring.flyway.enabled` | `FLYWAY_ENABLED` | `true` | Enable/disable Flyway |
| `spring.flyway.baseline-on-migrate` | `FLYWAY_BASELINE_ON_MIGRATE` | `false` | Baseline existing DB |

### Profiles

| Profile | File | Purpose |
|---------|------|---------|
| `default` | `application.properties` | Main configuration |
| `sql` | `application-sql.properties` | Enables Hibernate SQL logging, bind parameter tracing, and statistics |

---

## Security

### Authentication

JWT-based stateless authentication using Bearer tokens.

**Flow:**
1. Client sends `POST /api/auth/login` with credentials
2. Server validates and returns a JWT token
3. Client includes `Authorization: Bearer <token>` on subsequent requests
4. `JwtAuthenticationFilter` extracts and validates the token, loading `AccountPrincipal`

**Token payload:**
- `sub` — Account UUID
- `roles` — Sorted list of role codes (e.g., `["ADMIN", "DOCTOR"]`)

### Roles & Hierarchy

| Code | Name | Description |
|------|------|-------------|
| `GOD` | Head Administrator | Inherits all other roles |
| `ADMIN` | Administrator | User, staff, and report management |
| `DOCTOR` | Doctor | Medical checks, donations, examinations |
| `NURSE` | Nurse | Blood collection sessions |
| `LAB` | Lab Technician | Sample processing, lab results |
| `DONOR` | Donor | Self-service (profile, bookings, history) |

`ROLE_GOD` inherits all other roles via Spring Security's role hierarchy.

### Rate Limiting

| Endpoint | Limit |
|----------|-------|
| `POST /api/auth/login` | 5 requests / minute per IP |
| `POST /api/auth/register` | 3 requests / minute per IP |

Implemented using Bucket4j with `ConcurrentHashMap` storage.

### CORS

Configurable via `cors.allowed-origins`. Allowed methods: `GET, POST, PUT, PATCH, DELETE, OPTIONS`. Allowed headers: `Content-Type, Authorization, X-Request-Id`.

### Public Endpoints

The following paths do not require authentication:
- `/api/auth/**`
- `/actuator/health`
- `/error`
- `/v3/api-docs/**`
- `/swagger-ui/**`

### Password Encoding

BCrypt via Spring Security's `PasswordEncoder`.

---

## REST API Reference

Base URL: `http://localhost:<port>/api`

### Auth (`/api/auth`) — Public

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/register` | Register a new donor account |
| `POST` | `/login` | Authenticate (email or phone) |
| `GET` | `/me` | Get current user profile (requires token) |

<details>
<summary>Request / Response DTOs</summary>

**`RegisterRequest`**
```json
{
  "email": "string (optional)",
  "phone": "string (optional)",
  "password": "string",
  "lastName": "string",
  "firstName": "string",
  "middleName": "string (optional)",
  "birthDate": "yyyy-MM-dd"
}
```

**`LoginRequest`**
```json
{
  "identifier": "string (email or phone)",
  "password": "string"
}
```

**`AuthResponse`** (`201` / `200`)
```json
{
  "token": "string",
  "accountId": "uuid",
  "roles": ["DONOR"]
}
```

**`AccountProfileResponse`** (`200`)
```json
{
  "accountId": "uuid",
  "email": "string",
  "phone": "string",
  "roles": ["DONOR"],
  "profileType": "string",
  "fullName": "string"
}
```
</details>

---

### Admin — Accounts (`/api/admin`) — `ADMIN`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/accounts` | Create a new account |
| `PATCH` | `/accounts/{accountId}` | Update account (active status / password) |
| `POST` | `/accounts/{accountId}/roles` | Assign roles to account |
| `POST` | `/staff-profiles` | Create a staff profile |
| `GET` | `/staff` | List staff (filter: `?role=`, `?staffKind=`) |
| `GET` | `/donors` | List all donors |

### Admin — Operations (`/api/admin`) — `ADMIN`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/donors/phone-registration` | Register donor by phone |
| `GET` | `/reminders/eligible` | List donors eligible for re-donation (`?minDaysSinceDonation=56`) |
| `POST` | `/reminders/eligible/{donorId}/mark-notified` | Mark eligible donor as notified |
| `GET` | `/documents/expired` | List expired documents (`?asOf=`) |
| `POST` | `/documents/expired/{documentId}/mark-notified` | Mark expired document as notified |
| `GET` | `/reports/summary` | Dashboard reports summary (`?from=`, `?to=`) |
| `POST` | `/reminders/send` | Send a reminder to a donor |

### Admin — Report Requests (`/api/admin/reports/requests`) — `ADMIN`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List all report requests (`?status=`) |
| `POST` | `/{requestId}/take` | Take ownership of a request |
| `POST` | `/{requestId}/generate` | Generate report content |
| `POST` | `/{requestId}/send` | Send generated report to requester |
| `POST` | `/{requestId}/reject` | Reject a report request |

---

### Appointments (`/api/appointments`)

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `GET` | `/slots` | Any authenticated | List available slots (`?from=`, `?purpose=`) |
| `POST` | `/slots` | `ADMIN` / `DOCTOR` / `NURSE` | Create a new slot |
| `POST` | `/bookings` | `DONOR` | Book an appointment |
| `GET` | `/bookings/my` | `DONOR` | List own bookings |
| `POST` | `/bookings/{bookingId}/cancel` | `DONOR` | Cancel a booking |
| `POST` | `/bookings/{bookingId}/reschedule` | `DONOR` | Reschedule a booking |

<details>
<summary>Key DTOs</summary>

**`CreateSlotRequest`**
```json
{
  "purpose": "EXAMINATION | DONATION | CHECKUP",
  "startAt": "ISO-8601 datetime",
  "endAt": "ISO-8601 datetime",
  "location": "string",
  "capacity": 5
}
```

**`CreateBookingRequest`**
```json
{
  "slotId": "uuid"
}
```

**`BookingStatus`**: `PENDING_QUESTIONNAIRE`, `CONFIRMED`, `BOOKED`, `CANCELLED`, `COMPLETED`
</details>

---

### Donor (`/api/donor`) — `DONOR`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/profile` | Get donor profile |
| `PUT` | `/profile` | Update donor profile |
| `POST` | `/consents` | Submit a consent form |
| `GET` | `/donations` | Donation history |
| `GET` | `/test-results` | Published lab test results |
| `GET` | `/visits` | Visit history |
| `GET` | `/eligibility` | Check donation eligibility |
| `GET` | `/notifications` | List notifications |
| `POST` | `/notifications/{deliveryId}/ack` | Acknowledge a notification |

### Donor Examination (`/api/donor/examination`) — `DONOR`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/slots` | List examination time slots (`?from=`, `?to=`) |
| `POST` | `/bookings` | Book an examination |
| `GET` | `/bookings/{bookingId}` | Get booking details |
| `POST` | `/bookings/{bookingId}/confirm` | Confirm with questionnaire & consent |
| `DELETE` | `/bookings/{bookingId}` | Cancel an examination booking |

---

### Medical Workflow (`/api/medical`) — `DOCTOR`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/queue` | List donor queue for medical checks (`?from=`) |
| `POST` | `/checks` | Record a medical check result |
| `POST` | `/donations` | Register a donation |
| `POST` | `/donations/{donationId}/publish` | Publish a donation record |
| `POST` | `/samples` | Register a sample from a donation |
| `POST` | `/reactions` | Report an adverse reaction |
| `PATCH` | `/donors/{donorId}/status` | Update donor status |
| `GET` | `/examinations/pending` | List pending examinations |
| `GET` | `/examinations/queue` | Examination queue (`?from=`) |
| `POST` | `/examinations/{visitId}/lab-request` | Create a lab examination request |
| `POST` | `/examinations/{visitId}/decision` | Make examination decision |
| `POST` | `/examinations/review` | Review examination results |

<details>
<summary>Key DTOs</summary>

**`MedicalCheckRequest`**
```json
{
  "visitId": "uuid",
  "weightKg": 70.5,
  "hemoglobinGl": 140.0,
  "hematocritPct": 42.0,
  "rbc10e12L": 4.5,
  "systolicMmhg": 120,
  "diastolicMmhg": 80,
  "pulseRate": 72,
  "bodyTemperatureC": 36.6,
  "decision": "ADMITTED | REFUSED"
}
```

**`DonationRequest`**
```json
{
  "visitId": "uuid",
  "donationType": "WHOLE_BLOOD | PLASMA | PLATELETS | ERYTHROCYTES | GRANULOCYTES",
  "volumeMl": 450
}
```
</details>

---

### Nurse Workflow (`/api/nurse`) — `NURSE`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/donations/queue` | List donors ready for blood collection (`?from=`) |
| `POST` | `/collection-sessions` | Create a collection session |
| `POST` | `/collection-sessions/{id}/start` | Start collection |
| `POST` | `/collection-sessions/{id}/complete` | Complete collection |
| `POST` | `/collection-sessions/{id}/abort` | Abort collection |
| `GET` | `/collection-sessions/{id}` | Get session details |

**Collection Session Status Flow:** `PREPARED` → `IN_PROGRESS` → `COMPLETED` | `ABORTED`

---

### Lab Workflow (`/api/lab`) — `LAB`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/queue` | List lab examination queue |
| `GET` | `/samples` | List pending samples (`?status=`) |
| `POST` | `/results` | Record a lab test result |
| `POST` | `/results/{resultId}/publish` | Publish a lab result |
| `GET` | `/samples/{sampleId}/results` | Get results for a sample |
| `POST` | `/examinations/{requestId}/results` | Submit examination lab results |
| `GET` | `/examinations/pending` | List pending examination requests |
| `GET` | `/examinations/requests` | List all examination requests |

---

### Report Requests (`/api/reports`) — `DOCTOR` / `LAB` / `NURSE`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/requests` | Create a report request for a donor |
| `GET` | `/requests/mine` | List my submitted requests |
| `GET` | `/{requestId}` | Get report details |

**Report Types:** `DONOR_SUMMARY`, `LAB_OVERVIEW`, `DONATION_HISTORY`, `ELIGIBILITY`, `INCIDENTS`

**Report Status Flow:** `REQUESTED` → `IN_PROGRESS` → `READY` → `SENT` | `REJECTED`

---

### Role Management (`/api/roles`) — `ADMIN` / `GOD`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/` | List all roles |
| `GET` | `/accounts/{accountId}` | Get roles for an account |
| `POST` | `/assign` | Assign a role to an account |
| `POST` | `/remove` | Remove a role from an account |

> **Note:** The `GOD` role can only be managed by `GOD` users.

---

### Staff (`/api/staff`) — `NURSE` / `LAB` / `DOCTOR` / `ADMIN` / `GOD`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/donors` | List donors (`?status=`) |
| `GET` | `/donors/{donorId}/report` | Detailed donor report |

---

## Domain Model

### Entity Relationship Diagram

```
Account ──1:N──> account_role <──N:1── Role
Account ──1:1──> DonorProfile
Account ──1:1──> StaffProfile

DonorProfile ──1:N──> Booking
DonorProfile ──1:N──> DonorDocument
DonorProfile ──1:N──> Consent
DonorProfile ──1:N──> Questionnaire
DonorProfile ──1:N──> Deferral
DonorProfile ──1:N──> ReportRequest
DonorProfile ──1:N──> NotificationDelivery

Booking ──N:1──> AppointmentSlot
Booking ──1:1──> Visit

Visit ──1:1──> MedicalCheck
Visit ──1:1──> Donation
Visit ──1:1──> CollectionSession
Visit ──1:1──> LabExaminationRequest
Visit ──1:N──> Consent
Visit ──1:N──> Questionnaire

Donation ──1:N──> Sample
Donation ──1:N──> AdverseReaction
Donation ──1:N──> BloodUnit

Sample ──1:N──> LabTestResult
LabTestResult ──N:1──> LabTestType

BloodUnit ──N:1──> BloodComponentType

Notification ──1:N──> NotificationDelivery
```

### Core Entities

#### Account
The base user entity. Must have at least an email or phone number.

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `email` | `String` | Unique, nullable |
| `phone` | `String` | Unique, nullable |
| `lastName` | `String` | |
| `firstName` | `String` | |
| `middleName` | `String` | Nullable |
| `passwordHash` | `String` | BCrypt |
| `active` | `boolean` | Default `true` |
| `createdAt` | `OffsetDateTime` | Auto-set |
| `roles` | `Set<Role>` | ManyToMany |

#### DonorProfile

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `account` | `Account` | OneToOne |
| `birthDate` | `LocalDate` | |
| `bloodGroup` | `BloodGroup` | `I` (O), `II` (A), `III` (B), `IV` (AB) |
| `rhFactor` | `RhFactor` | `POSITIVE` (+), `NEGATIVE` (-) |
| `donorStatus` | `DonorStatus` | `POTENTIAL`, `ACTIVE` |

#### StaffProfile

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `account` | `Account` | OneToOne |
| `staffKind` | `String` | `ADMIN`, `DOCTOR`, `NURSE`, `LAB` |

#### AppointmentSlot

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `purpose` | `SlotPurpose` | `EXAMINATION`, `DONATION`, `CHECKUP` |
| `startAt` | `OffsetDateTime` | Must be before `endAt` |
| `endAt` | `OffsetDateTime` | |
| `location` | `String` | |
| `capacity` | `Integer` | > 0 |

#### Booking

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `donor` | `DonorProfile` | ManyToOne |
| `slot` | `AppointmentSlot` | ManyToOne |
| `status` | `BookingStatus` | See enum |
| `createdAt` | `OffsetDateTime` | |
| `cancelledAt` | `OffsetDateTime` | Nullable, auto-set by trigger on CANCELLED |

Unique index: `(donor_id, slot_id)` where `cancelled_at IS NULL`.

#### Visit

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | Primary key |
| `booking` | `Booking` | OneToOne |
| `checkInAt` | `OffsetDateTime` | Nullable |
| `visitStatus` | `String` | Default `SCHEDULED` |

#### MedicalCheck

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `visit` | `Visit` | OneToOne |
| `weightKg` | `BigDecimal` | |
| `hemoglobinGl` | `BigDecimal` | |
| `hematocritPct` | `BigDecimal` | |
| `rbc10e12L` | `BigDecimal` | |
| `systolicMmhg` | `Integer` | |
| `diastolicMmhg` | `Integer` | |
| `pulseRate` | `Integer` | |
| `bodyTemperatureC` | `BigDecimal` | |
| `decision` | `MedicalCheckDecision` | `PENDING_REVIEW`, `ADMITTED`, `REFUSED` |

#### Donation

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `visit` | `Visit` | OneToOne |
| `donationType` | `DonationType` | `WHOLE_BLOOD`, `PLASMA`, `PLATELETS`, `ERYTHROCYTES`, `GRANULOCYTES` |
| `volumeMl` | `Integer` | |
| `performedBy` | `StaffProfile` | |
| `published` | `boolean` | Default `false` |
| `publishedAt` | `OffsetDateTime` | Managed by DB trigger |

#### Sample

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `donation` | `Donation` | ManyToOne |
| `sampleCode` | `String` | Unique |
| `status` | `SampleStatus` | `NEW`, `REGISTERED`, `QUARANTINE`, `REJECTED`, `PROCESSED` |
| `quarantineReason` | `String` | Auto-cleared by trigger |
| `rejectionReason` | `String` | Auto-cleared by trigger |

#### Deferral

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `donor` | `DonorProfile` | ManyToOne |
| `deferralType` | `DeferralType` | `TEMPORARY`, `PERMANENT` |
| `reason` | `String` | |
| `startsAt` | `OffsetDateTime` | |
| `endsAt` | `OffsetDateTime` | Null for permanent deferrals |

#### CollectionSession

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `visit` | `Visit` | OneToOne |
| `nurse` | `StaffProfile` | Nullable |
| `status` | `CollectionSessionStatus` | `PREPARED`, `IN_PROGRESS`, `COMPLETED`, `ABORTED` |
| Pre/Post-vitals | `BigDecimal` / `Integer` | Blood pressure, pulse, temperature, wellbeing |

#### LabTestResult

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `sample` | `Sample` | ManyToOne |
| `testType` | `LabTestType` | ManyToOne |
| `resultValue` | `String` | |
| `resultFlag` | `String` | |
| `published` | `boolean` | |
| `publishedAt` | `OffsetDateTime` | Managed by DB trigger |

Unique constraint: `(sample_id, test_type_id)`.

#### BloodUnit

| Field | Type | Notes |
|-------|------|-------|
| `id` | `UUID` | |
| `donation` | `Donation` | ManyToOne |
| `componentType` | `BloodComponentType` | `RBC`, `PLASMA`, `PLATELETS` |
| `bloodGroup` | `String` | |
| `rhFactor` | `String` | |
| `volumeMl` | `Integer` | |
| `expiresAt` | `OffsetDateTime` | |
| `status` | `String` | Default `IN_STOCK` |

A database view `v_blood_unit` adds `effective_status` — automatically shows `EXPIRED` when past expiry.

---

## Business Workflows

### Donation Workflow

```
[Donor] ──books──> [AppointmentSlot (DONATION)]
     │
     └──> [Booking (BOOKED)] ──creates──> [Visit (SCHEDULED)]
              │
              └──> [Doctor: MedicalCheck]
                      │
                  ┌───┴───┐
              ADMITTED   REFUSED ──> [Deferral]
                  │
                  └──> [Nurse: CollectionSession]
                          PREPARED → IN_PROGRESS → COMPLETED
                          │
                          └──> [Doctor: Donation]
                                  │
                                  ├──> Publish donation
                                  └──> [Sample (NEW)]
                                          │
                                          └──> [Lab: LabTestResult]
                                                  │
                                                  └──> Publish results → Donor notified
```

### Examination Workflow

```
[Donor] ──books──> [AppointmentSlot (EXAMINATION)]
     │
     └──> [Booking (PENDING_QUESTIONNAIRE)]
              │
              └──> Donor confirms (Questionnaire + Consent)
                      │
                      └──> [Booking (CONFIRMED)] ──> [Visit]
                              │
                              └──> [Doctor: LabExaminationRequest]
                                      │
                                      └──> [Lab: Submit results]
                                              │
                                              └──> [Doctor: Review & Decision]
                                                      ADMITTED / REFUSED
```

### Report Request Workflow

```
[Staff] ──creates──> [ReportRequest (REQUESTED)]
     │
     └──> [Admin takes ownership] ──> (IN_PROGRESS)
              │
              ├──> [Generate report] ──> (READY) ──> [Send] ──> (SENT)
              └──> [Reject] ──> (REJECTED)
```

---

## Event System

The application uses Spring's `ApplicationEventPublisher` for decoupled event handling.

### Domain Events

All events implement the sealed interface `DomainEvent`:

| Event | Payload | Purpose |
|-------|---------|---------|
| `AuditDomainEvent` | `accountId`, `action`, `entityType`, `entityId`, `metadata`, `occurredAt` | Audit trail logging |
| `NotificationDomainEvent` | `donor`, `topic`, `body`, `channel`, `staffAccountId`, `occurredAt` | Donor notification delivery |

### Processing

- Events are published within the originating transaction
- Listeners process events **after commit** (`@TransactionalEventListener(phase = AFTER_COMMIT)`)
- Listener failures are logged but **do not** affect the main transaction

---

## Notification System

### Notification Topics

| Topic | Trigger |
|-------|---------|
| `donation-complete` | Donation registered |
| `donation-results` | Lab results published |
| `post-donation-care` | After donation |
| `eligibility` | Eligibility check |
| `revisit-reminder` | Re-donation reminder |
| `medical-check` | Medical check completed |
| `adverse-reaction` | Adverse reaction reported |
| `deferral` | Deferral created |
| `report-request` | Report requested |
| `report-ready` | Report generated |
| `expired-docs` | Document expiration |
| `appointment-reminder` | Appointment upcoming |
| `appointment-confirmed` | Booking confirmed |
| `appointment-cancelled` | Booking cancelled |
| `lab-results-ready` | Lab results available |

### Delivery

- `NotificationService` creates `Notification` + `NotificationDelivery` records
- Default channel: `email`
- Delivery status flow: `PENDING` → `SENT` → `ACKED`
- Donors can acknowledge notifications via `POST /api/donor/notifications/{deliveryId}/ack`

---

## Exception Handling

### Exception Hierarchy

```
ApplicationException (abstract)
├── NotFoundException         → 404  (NOT_FOUND)
├── BadRequestException       → 400  (BAD_REQUEST)
├── BusinessRuleException     → 409  (BUSINESS_RULE_VIOLATION)
├── ConflictException         → 409  (CONFLICT)
├── ForbiddenException        → 403  (FORBIDDEN)
└── ValidationException       → 422  (VALIDATION_ERROR)
```

### Error Response Format

All errors return a unified JSON structure:

```json
{
  "code": "NOT_FOUND",
  "message": "Donor profile not found",
  "details": {},
  "timestamp": "2026-02-11T12:00:00Z",
  "path": "/api/donor/profile"
}
```

### Global Handler (`@RestControllerAdvice`)

| Exception | HTTP Status |
|-----------|-------------|
| `ApplicationException` subclasses | Per subclass (see above) |
| `MethodArgumentNotValidException` | `400` |
| `ConstraintViolationException` | `400` |
| `MissingServletRequestParameterException` | `400` |
| `HttpMessageNotReadableException` | `400` |
| `IllegalArgumentException` | `400` |
| `AuthenticationException` | `401` |
| `AccessDeniedException` | `403` |
| `NoResourceFoundException` | `404` |
| `HttpRequestMethodNotSupportedException` | `405` |
| `IllegalStateException` | `409` |
| `HttpMediaTypeNotSupportedException` | `415` |
| Unhandled `Exception` | `500` (generic message) |

---

## Database

### Migrations (Flyway)

| Version | File | Description |
|---------|------|-------------|
| V1 | `V1__init.sql` | Creates all tables, indexes, triggers, views, and seeds reference data |
| V2 | `V2__critical_query_functions.sql` | Optimized SQL functions and indexes for read-path performance |

### Tables (22 total)

`account`, `role`, `account_role`, `staff_profile`, `donor_profile`, `donor_document`, `appointment_slot`, `booking`, `visit`, `consent`, `questionnaire`, `medical_check`, `donation`, `sample`, `lab_test_type`, `lab_test_result`, `blood_component_type`, `blood_unit`, `deferral`, `adverse_reaction`, `collection_session`, `lab_examination_request`, `contraindication`, `notification`, `notification_delivery`, `report_request`, `audit_event`

### Views

- **`v_blood_unit`** — Extends `blood_unit` with `effective_status` column that shows `EXPIRED` when `expires_at < now()`

### Database Triggers

| Trigger | Table | Purpose |
|---------|-------|---------|
| `trg_booking_consistency` | `booking` | Normalizes status values, auto-sets `cancelled_at` |
| `trg_publication_consistency` | `donation`, `lab_test_result` | Syncs `published_at` with `is_published` flag |
| `trg_notification_delivery_consistency` | `notification_delivery` | Validates donor/staff reference, syncs `sent_at` |
| `trg_sample_consistency` | `sample` | Clears irrelevant reason fields based on sample status |
| `trg_blood_unit_consistency` | `blood_unit` | Validates volume and expiry constraints |

### Optimized Query Functions (V2)

| Function | Purpose |
|----------|---------|
| `fn_exists_active_booking(donor_id, slot_id)` | Check for existing active booking |
| `fn_count_active_bookings_by_slot(slot_id)` | Count active bookings for capacity validation |
| `fn_get_pending_booking_id(donor_id, slot_id, status)` | Find pending booking |
| `fn_get_active_deferral_id(donor_id, now)` | Find active deferral |
| `fn_get_latest_medical_check_id(donor_id)` | Find latest medical check |

### Reference Data (Seeded)

**Roles:** `GOD`, `ADMIN`, `DOCTOR`, `NURSE`, `LAB`, `DONOR`

**Lab Test Types:** `HIV`, `HBSAG`, `HCV`, `BLOOD_GROUP`, `RH`

**Blood Component Types:** `RBC`, `PLASMA`, `PLATELETS`

---

## Testing

The project uses **JUnit 5** with **Testcontainers** for integration testing against a real PostgreSQL instance.

```bash
# Run all tests
./gradlew test

# Test reports
build/reports/tests/test/index.html
```

Test configuration is in `src/test/resources/application-test.yml`.

---

## Deployment

### Docker

```bash
# Build and run with Docker Compose
docker compose up --build

# Build JAR only
./gradlew clean bootJar

# Build Docker image
docker build -t blood-donor-backend .
```

### Manual Deployment

```bash
# Build the JAR
./gradlew clean bootJar

# Deploy the JAR (example: copy to remote server)
scp build/libs/coursach_back-0.0.1-SNAPSHOT.jar user@server:~

# Run on server
java -jar coursach_back-0.0.1-SNAPSHOT.jar \
  --spring.datasource.url=jdbc:postgresql://host:5432/blood_donation \
  --security.jwt.secret=your-secret-key
```

### Docker Compose Services

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| `postgres` | `postgres:16` | `5432` | PostgreSQL database |
| `app` | Built from `Dockerfile` | `8080` | Spring Boot application |

### Required Environment Variables (Production)

| Variable | Description |
|----------|-------------|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |
| `SECURITY_JWT_SECRET` | JWT signing key (≥256 bits) |
| `CORS_ORIGINS` | Comma-separated allowed origins |
