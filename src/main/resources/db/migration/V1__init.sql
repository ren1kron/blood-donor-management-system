-- PostgreSQL schema for Blood Donation Management System.
create extension if not exists pgcrypto;

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
    id              uuid primary key default gen_random_uuid(),
    name            text not null,
    is_permanent    boolean not null default false,
    description     text
);

create index idx_contraindication_name on contraindication (name);

create table account (
    id              uuid primary key default gen_random_uuid(),
    email           text unique,
    phone           text unique,
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
    full_name    text not null,
    birth_date   date not null,
    blood_group  text,
    rh_factor    text,
    donor_status text not null default 'ACTIVE'
);

create index idx_donor_profile_full_name on donor_profile (full_name);

create table staff_profile (
    id          uuid primary key default gen_random_uuid(),
    account_id  uuid not null unique references account(id) on delete cascade,
    full_name   text not null,
    staff_kind  text not null
);

create index idx_staff_profile_full_name on staff_profile (full_name);

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
    cancelled_at timestamptz,
    constraint uq_booking_donor_slot unique (donor_id, slot_id)
);

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

create table questionnaire (
    id           uuid primary key default gen_random_uuid(),
    visit_id     uuid not null references visit(id) on delete cascade,
    donor_id     uuid not null references donor_profile(id) on delete cascade,
    filled_at    timestamptz not null default now(),
    payload_json jsonb not null
);

create index idx_questionnaire_visit on questionnaire (visit_id);
create index idx_questionnaire_payload_gin on questionnaire using gin (payload_json);

create table medical_check (
    id                      uuid primary key default gen_random_uuid(),
    visit_id                uuid not null unique references visit(id) on delete cascade,
    performed_by_staff_id   uuid not null references staff_profile(id) on delete restrict,
    weight_kg               numeric(5,2),
    hemoglobin_g_l          numeric(6,2),
    systolic_mmhg           integer,
    diastolic_mmhg          integer,
    decision                text not null,
    decision_at             timestamptz not null default now()
);

create index idx_medical_check_staff on medical_check (performed_by_staff_id);

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
    performed_at            timestamptz not null default now()
);

create index idx_donation_staff on donation (performed_by_staff_id);

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
    donation_id          uuid not null references donation(id) on delete cascade,
    component_type_id    smallint not null references blood_component_type(id) on delete restrict,
    blood_group          text,
    rh_factor            text,
    volume_ml            integer,
    collected_at         timestamptz not null default now(),
    expires_at           timestamptz,
    status               text not null default 'IN_STOCK',
    storage_location     text
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

insert into role (code, name)
values
    ('DONOR', 'Donor'),
    ('ADMIN', 'Administrator'),
    ('DOCTOR', 'Doctor'),
    ('NURSE', 'Nurse'),
    ('LAB', 'Lab Technician'),
    ('REGISTRAR', 'Registrar'),
    ('GOD', 'Head Administrator')
on conflict do nothing;

with god_account as (
    insert into account (email, password_hash)
    values ('god@system.local', crypt('big_papa', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
god_account_id as (
    select id from god_account
    union
    select id from account where email = 'god@system.local'
),
god_role as (
    select id from role where code = 'GOD'
)
insert into account_role (account_id, role_id)
select a.id, r.id
from god_account_id a
cross join god_role r
on conflict do nothing;

with donor_account as (
    insert into account (email, password_hash)
    values ('donor@system.local', crypt('donor_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
donor_account_id as (
    select id from donor_account
    union
    select id from account where email = 'donor@system.local'
),
donor_role as (
    select id from role where code = 'DONOR'
),
donor_profile_seed as (
    insert into donor_profile (account_id, full_name, birth_date, blood_group, rh_factor, donor_status)
    select id, 'Demo Donor Nikolaevich', date '1995-05-15', 'I', '+', 'ACTIVE'
    from donor_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from donor_account_id a
cross join donor_role r
on conflict do nothing;

with admin_account as (
    insert into account (email, password_hash)
    values ('admin@system.local', crypt('admin_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
admin_account_id as (
    select id from admin_account
    union
    select id from account where email = 'admin@system.local'
),
admin_role as (
    select id from role where code = 'ADMIN'
),
admin_profile_seed as (
    insert into staff_profile (account_id, full_name, staff_kind)
    select id, 'Demo Admin Ignatevich', 'ADMIN'
    from admin_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from admin_account_id a
cross join admin_role r
on conflict do nothing;

with doctor_account as (
    insert into account (email, password_hash)
    values ('doctor@system.local', crypt('doctor_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
doctor_account_id as (
    select id from doctor_account
    union
    select id from account where email = 'doctor@system.local'
),
doctor_role as (
    select id from role where code = 'DOCTOR'
),
doctor_profile_seed as (
    insert into staff_profile (account_id, full_name, staff_kind)
    select id, 'Demo Doctor Romanovich', 'DOCTOR'
    from doctor_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from doctor_account_id a
cross join doctor_role r
on conflict do nothing;

with nurse_account as (
    insert into account (email, password_hash)
    values ('nurse@system.local', crypt('nurse_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
nurse_account_id as (
    select id from nurse_account
    union
    select id from account where email = 'nurse@system.local'
),
nurse_role as (
    select id from role where code = 'NURSE'
),
nurse_profile_seed as (
    insert into staff_profile (account_id, full_name, staff_kind)
    select id, 'Demo Nurse Ivanovna', 'NURSE'
    from nurse_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from nurse_account_id a
cross join nurse_role r
on conflict do nothing;

with lab_account as (
    insert into account (email, password_hash)
    values ('lab@system.local', crypt('lab_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
lab_account_id as (
    select id from lab_account
    union
    select id from account where email = 'lab@system.local'
),
lab_role as (
    select id from role where code = 'LAB'
),
lab_profile_seed as (
    insert into staff_profile (account_id, full_name, staff_kind)
    select id, 'Demo Lab Tech', 'LAB'
    from lab_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from lab_account_id a
cross join lab_role r
on conflict do nothing;

with registrar_account as (
    insert into account (email, password_hash)
    values ('registrar@system.local', crypt('registrar_pass', gen_salt('bf')))
    on conflict (email) do nothing
    returning id
),
registrar_account_id as (
    select id from registrar_account
    union
    select id from account where email = 'registrar@system.local'
),
registrar_role as (
    select id from role where code = 'REGISTRAR'
),
registrar_profile_seed as (
    insert into staff_profile (account_id, full_name, staff_kind)
    select id, 'Demo Registrar Petrovich', 'REGISTRAR'
    from registrar_account_id
    on conflict (account_id) do nothing
)
insert into account_role (account_id, role_id)
select a.id, r.id
from registrar_account_id a
cross join registrar_role r
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
