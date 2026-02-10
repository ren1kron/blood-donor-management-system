-- PostgreSQL schema for Blood Donation Management System.

create table role (
    id          smallserial primary key,
    code        text not null unique,
    name        text not null
);

create table lab_test_type (
    id          smallserial primary key,
    code        text not null unique,
    name        text not null
);

create table blood_component_type (
    id          smallserial primary key,
    code        text not null unique,
    name        text not null
);

create table contraindication (
    id              uuid primary key,
    name            text not null,
    is_permanent    boolean not null default false,
    description     text
);

create index idx_contraindication_name on contraindication (name);

create table account (
    id              uuid primary key default gen_random_uuid(),
    email           text unique,
    phone           text unique,
    last_name       text,
    first_name      text,
    middle_name     text,
    password_hash   text not null,
    created_at      timestamptz not null default now(),
    is_active       boolean not null default true,
    constraint chk_account_contact
        check (email is not null or phone is not null)
);

create table account_role (
    account_id  uuid not null references account(id) on delete cascade,
    role_id     smallint not null references role(id) on delete restrict,
    granted_at  timestamptz not null default now(),
    primary key (account_id, role_id)
);

create index idx_account_role_role on account_role (role_id);

create table donor_profile (
    id           uuid primary key default gen_random_uuid(),
    account_id   uuid not null unique references account(id) on delete cascade,
    birth_date   date not null,
    blood_group  text,
    rh_factor    text,
    donor_status text not null default 'POTENTIAL'
);

create table staff_profile (
    id          uuid primary key default gen_random_uuid(),
    account_id  uuid not null unique references account(id) on delete cascade,
    staff_kind  text not null
);

create table donor_contraindication (
    donor_id            uuid not null references donor_profile(id) on delete cascade,
    contraindication_id uuid not null references contraindication(id) on delete restrict,
    detected_at         timestamptz not null default now(),
    resolved_at         timestamptz,
    primary key (donor_id, contraindication_id, detected_at)
);

create index idx_donor_contraindication_donor on donor_contraindication (donor_id);
create index idx_donor_contraindication_contra on donor_contraindication (contraindication_id);

create table appointment_slot (
    id          uuid primary key default gen_random_uuid(),
    purpose     text not null,
    start_at    timestamptz not null,
    end_at      timestamptz not null,
    location    text not null,
    capacity    integer not null default 1,
    constraint chk_slot_time check (end_at > start_at),
    constraint chk_slot_capacity check (capacity > 0)
);

create index idx_slot_start_at on appointment_slot (start_at);

create table booking (
    id           uuid primary key default gen_random_uuid(),
    donor_id     uuid not null references donor_profile(id) on delete cascade,
    slot_id      uuid not null references appointment_slot(id) on delete restrict,
    status       text not null default 'BOOKED',
    created_at   timestamptz not null default now(),
    cancelled_at timestamptz
);

create unique index uq_booking_donor_slot_active
    on booking (donor_id, slot_id)
    where cancelled_at is null;

create index idx_booking_donor on booking (donor_id);
create index idx_booking_slot on booking (slot_id);

create table visit (
    id           uuid primary key default gen_random_uuid(),
    booking_id   uuid not null unique references booking(id) on delete cascade,
    check_in_at  timestamptz,
    visit_status text not null default 'SCHEDULED'
);

create table consent (
    id           uuid primary key default gen_random_uuid(),
    visit_id     uuid not null references visit(id) on delete cascade,
    donor_id     uuid not null references donor_profile(id) on delete cascade,
    consent_type text not null,
    given_at     timestamptz not null default now()
);

create index idx_consent_visit on consent (visit_id);
create index idx_consent_donor on consent (donor_id);

create table questionnaire (
    id                          uuid primary key default gen_random_uuid(),
    visit_id                    uuid not null references visit(id) on delete cascade,
    donor_id                    uuid not null references donor_profile(id) on delete cascade,
    filled_at                   timestamptz not null default now(),
    has_fever                   boolean not null,
    took_antibiotics_last_14d   boolean not null,
    has_chronic_diseases        boolean not null,
    comment                     text
);

create index idx_questionnaire_visit on questionnaire (visit_id);
create index idx_questionnaire_donor on questionnaire (donor_id);

create table medical_check (
    id                      uuid primary key default gen_random_uuid(),
    visit_id                uuid not null unique references visit(id) on delete cascade,
    performed_by_staff_id   uuid references staff_profile(id) on delete restrict,
    submitted_by_lab_id     uuid references staff_profile(id),
    submitted_at            timestamptz,
    status                  text not null default 'PENDING_REVIEW',
    weight_kg               numeric(5,2),
    hemoglobin_g_l          numeric(6,2),
    hematocrit_pct          numeric(5,2),
    rbc_10e12_l             numeric(5,2),
    systolic_mmhg           integer,
    diastolic_mmhg          integer,
    pulse_rate              integer,
    body_temperature_c      numeric(4,2),
    decision                text not null,
    decision_at             timestamptz not null default now()
);

create index idx_medical_check_staff on medical_check (performed_by_staff_id);
create index idx_medical_check_status on medical_check (status);
create index idx_medical_check_lab on medical_check (submitted_by_lab_id);

create table lab_examination_request (
    id                      uuid primary key default gen_random_uuid(),
    visit_id                uuid not null unique references visit(id) on delete cascade,
    requested_by_staff_id   uuid not null references staff_profile(id) on delete restrict,
    requested_at            timestamptz not null default now(),
    status                  text not null default 'REQUESTED',
    completed_by_lab_id     uuid references staff_profile(id) on delete set null,
    completed_at            timestamptz,
    weight_kg               numeric(5,2),
    hemoglobin_g_l          numeric(6,2),
    hematocrit_pct          numeric(5,2),
    rbc_10e12_l             numeric(5,2),
    systolic_mmhg           integer,
    diastolic_mmhg          integer,
    pulse_rate              integer,
    body_temperature_c      numeric(4,2),
    blood_group             text,
    rh_factor               text
);

create index idx_lab_exam_request_status on lab_examination_request (status);
create index idx_lab_exam_request_requested_at on lab_examination_request (requested_at);
create index idx_lab_exam_request_requested_by on lab_examination_request (requested_by_staff_id);
create index idx_lab_exam_request_completed_by on lab_examination_request (completed_by_lab_id);
create index idx_lab_exam_request_hemoglobin on lab_examination_request (hemoglobin_g_l);

create table deferral (
    id                      uuid primary key default gen_random_uuid(),
    donor_id                uuid not null references donor_profile(id) on delete cascade,
    created_from_check_id   uuid references medical_check(id) on delete set null,
    deferral_type           text not null,
    reason                  text not null,
    starts_at               timestamptz not null default now(),
    ends_at                 timestamptz,
    constraint chk_deferral_time check (ends_at is null or ends_at > starts_at)
);

create index idx_deferral_donor on deferral (donor_id);

create table donation (
    id                      uuid primary key default gen_random_uuid(),
    visit_id                uuid not null unique references visit(id) on delete cascade,
    donation_type           text not null,
    volume_ml               integer,
    performed_by_staff_id   uuid not null references staff_profile(id) on delete restrict,
    performed_at            timestamptz not null default now(),
    is_published            boolean not null default false,
    published_at            timestamptz
);

create index idx_donation_staff on donation (performed_by_staff_id);
create index idx_donation_published on donation (is_published);

create table collection_session (
    id                  uuid primary key default gen_random_uuid(),
    visit_id            uuid not null unique references visit(id) on delete cascade,
    nurse_staff_id      uuid references staff_profile(id) on delete set null,
    status              text not null default 'PREPARED',
    started_at          timestamptz,
    ended_at            timestamptz,
    pre_systolic_mmhg       integer,
    pre_diastolic_mmhg      integer,
    pre_pulse_rate          integer,
    pre_body_temperature_c  numeric(4,2),
    pre_wellbeing           text,
    post_systolic_mmhg      integer,
    post_diastolic_mmhg     integer,
    post_pulse_rate         integer,
    post_body_temperature_c numeric(4,2),
    post_wellbeing          text,
    notes               text,
    complications       text,
    interruption_reason text,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now()
);

create index idx_collection_session_status on collection_session (status);
create index idx_collection_session_nurse on collection_session (nurse_staff_id);

create table adverse_reaction (
    id                      uuid primary key default gen_random_uuid(),
    donation_id             uuid not null references donation(id) on delete cascade,
    reported_by_staff_id    uuid references staff_profile(id) on delete set null,
    occurred_at             timestamptz not null default now(),
    severity                text,
    description             text
);

create index idx_reaction_donation on adverse_reaction (donation_id);

create table sample (
    id                  uuid primary key default gen_random_uuid(),
    donation_id         uuid not null references donation(id) on delete cascade,
    sample_code         text not null unique,
    collected_at        timestamptz not null default now(),
    status              text not null default 'NEW',
    quarantine_reason   text,
    rejection_reason    text
);

create index idx_sample_donation on sample (donation_id);

create table lab_test_result (
    id              uuid primary key default gen_random_uuid(),
    sample_id        uuid not null references sample(id) on delete cascade,
    test_type_id     smallint not null references lab_test_type(id) on delete restrict,
    labtech_staff_id uuid references staff_profile(id) on delete set null,
    result_value     text,
    result_flag      text not null,
    tested_at        timestamptz not null default now(),
    is_published     boolean not null default false,
    published_at     timestamptz,
    constraint uq_test_once_per_sample unique (sample_id, test_type_id)
);

create index idx_lab_result_sample on lab_test_result (sample_id);
create index idx_lab_result_type on lab_test_result (test_type_id);
create index idx_lab_result_staff on lab_test_result (labtech_staff_id);

create table blood_unit (
    id                  uuid primary key default gen_random_uuid(),
    donation_id         uuid not null references donation(id) on delete cascade,
    component_type_id   smallint not null references blood_component_type(id) on delete restrict,
    blood_group         text,
    rh_factor           text,
    volume_ml           integer,
    collected_at        timestamptz not null default now(),
    expires_at          timestamptz,
    status              text not null default 'IN_STOCK',
    storage_location    text
);

create index idx_blood_unit_donation on blood_unit (donation_id);
create index idx_blood_unit_component on blood_unit (component_type_id);
create index idx_blood_unit_status on blood_unit (status);

create table donor_document (
    id          uuid primary key default gen_random_uuid(),
    donor_id    uuid not null references donor_profile(id) on delete cascade,
    doc_type    text not null,
    issued_at   date,
    expires_at  date,
    status      text not null default 'VALID'
);

create index idx_donor_document_donor on donor_document (donor_id);
create index idx_donor_document_expires on donor_document (expires_at);

create table report_request (
    id                      uuid primary key default gen_random_uuid(),
    donor_id                uuid not null references donor_profile(id) on delete cascade,
    requested_by_staff_id   uuid not null references staff_profile(id) on delete restrict,
    requested_by_role       text,
    assigned_admin_id       uuid references staff_profile(id) on delete set null,
    report_type             text not null,
    status                  text not null default 'REQUESTED',
    payload_text            text,
    generated_at            timestamptz,
    message                 text,
    created_at              timestamptz not null default now(),
    updated_at              timestamptz not null default now()
);

create index idx_report_request_status on report_request (status);
create index idx_report_request_donor on report_request (donor_id);
create index idx_report_request_requested_by on report_request (requested_by_staff_id);
create index idx_report_request_assigned_admin on report_request (assigned_admin_id);

create table notification (
    id          uuid primary key default gen_random_uuid(),
    channel     text not null,
    topic       text not null,
    body        text not null,
    created_at  timestamptz not null default now()
);

create table notification_delivery (
    id              uuid primary key default gen_random_uuid(),
    notification_id uuid not null references notification(id) on delete cascade,
    donor_id        uuid references donor_profile(id) on delete cascade,
    staff_id        uuid references staff_profile(id) on delete set null,
    sent_at         timestamptz,
    status          text not null default 'PENDING'
);

create index idx_delivery_notification on notification_delivery (notification_id);
create index idx_delivery_donor on notification_delivery (donor_id);
create index idx_delivery_status on notification_delivery (status);
create index idx_notification_delivery_staff on notification_delivery (staff_id);

create table audit_event (
    id            uuid primary key default gen_random_uuid(),
    account_id    uuid references account(id) on delete set null,
    action        text not null,
    entity_type   text not null,
    entity_id     uuid,
    created_at    timestamptz not null default now(),
    metadata_text text
);

create index idx_audit_event_account on audit_event (account_id);
create index idx_audit_event_entity on audit_event (entity_type, entity_id);

insert into role (code, name)
values
    ('DONOR', 'Donor'),
    ('ADMIN', 'Administrator'),
    ('DOCTOR', 'Doctor'),
    ('NURSE', 'Nurse'),
    ('LAB', 'Lab Technician'),
    ('GOD', 'Head Administrator')
on conflict do nothing;

insert into lab_test_type (code, name)
values
    ('HIV', 'HIV'),
    ('HBSAG', 'Hepatitis B Surface Antigen'),
    ('HCV', 'Hepatitis C Virus'),
    ('BLOOD_GROUP', 'Blood Group'),
    ('RH', 'Rh Factor')
on conflict do nothing;

insert into blood_component_type (code, name)
values
    ('RBC', 'Red Blood Cells'),
    ('PLASMA', 'Plasma'),
    ('PLATELETS', 'Platelets')
on conflict do nothing;
