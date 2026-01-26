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
    full_name       text not null,
    created_at      timestamptz not null default now(),
    is_active       boolean not null default true,
    constraint chk_account_contact
        check (email is not null or phone is not null)
);

create index idx_account_full_name on account (full_name);

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
    donor_status text not null default 'ACTIVE',
    constraint chk_donor_status
        check (upper(donor_status) in ('ACTIVE', 'INACTIVE', 'DEFERRED'))
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
    cancelled_at timestamptz,
    constraint chk_booking_status
        check (upper(status) in ('BOOKED', 'CANCELLED')),
    constraint uq_booking_donor_slot unique (donor_id, slot_id)
);

create index idx_booking_donor on booking (donor_id);
create index idx_booking_slot on booking (slot_id);

create table visit (
    id           uuid primary key default gen_random_uuid(),
    booking_id   uuid not null unique references booking(id) on delete cascade,
    check_in_at  timestamptz,
    visit_status text not null default 'SCHEDULED',
    constraint chk_visit_status
        check (upper(visit_status) in ('SCHEDULED', 'DONE'))
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
    rejection_reason    text,
    constraint chk_sample_status
        check (upper(status) in ('NEW', 'REGISTERED', 'QUARANTINED', 'REJECTED'))
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
    storage_location     text,
    constraint chk_blood_unit_status
        check (upper(status) in ('IN_STOCK', 'RESERVED', 'ISSUED', 'DISCARDED'))
);

-- view для вычисляемого статуса blood_unit:
-- если срок годности истёк, возвращает EXPIRED,
-- не изменяя физическое состояние строки в таблице
create view v_blood_unit as
select
  bu.*,
  case
    when bu.expires_at is not null and bu.expires_at <= now()
         and upper(bu.status) in ('IN_STOCK', 'RESERVED')
      then 'EXPIRED'
    else upper(bu.status)
  end as effective_status
from blood_unit bu;

create index idx_blood_unit_donation on blood_unit (donation_id);
create index idx_blood_unit_component on blood_unit (component_type_id);
create index idx_blood_unit_status on blood_unit (status);

create table donor_document (
    id          uuid primary key default gen_random_uuid(),
    donor_id    uuid not null references donor_profile(id) on delete cascade,
    doc_type    text not null,
    issued_at   date,
    expires_at  date,
    status      text not null default 'VALID',
    constraint chk_document_status
        check (upper(status) in ('VALID', 'EXPIRED'))
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
    status          text not null default 'PENDING',
    constraint chk_delivery_status
        check (upper(status) in ('PENDING', 'SENT', 'ACKED'))
);

create index idx_delivery_notification on notification_delivery (notification_id);
create index idx_delivery_donor on notification_delivery (donor_id);
create index idx_delivery_status on notification_delivery (status);

-- Универсальная функция валидации полей группы крови и резус-фактора.
-- Используется разными сущностями для централизованной бизнес-валидации.
-- p_required = true  → поля обязательны
-- p_context  → имя сущности для текста ошибки
create or replace function fn_validate_blood_fields(
    p_blood_group text,
    p_rh_factor text,
    p_required boolean,
    p_context text
)
returns void
language plpgsql
as $$
begin
    -- Если оба поля отсутствуют
    if p_blood_group is null and p_rh_factor is null then
        if p_required then
            raise exception '% requires blood_group and rh_factor', p_context;
        end if;
        return;
    end if;

    -- Если заполнено только одно из двух полей
    if p_blood_group is null or p_rh_factor is null then
        raise exception '% requires both blood_group and rh_factor when one is provided', p_context;
    end if;

    -- Проверка допустимых значений группы крови
    if upper(p_blood_group) not in ('A', 'B', 'AB', 'O') then
        raise exception 'Invalid blood_group % in % (allowed: A, B, AB, O)', p_blood_group, p_context;
    end if;

    -- Проверка допустимых значений резус-фактора
    if upper(p_rh_factor) not in ('POSITIVE', 'NEGATIVE') then
        raise exception 'Invalid rh_factor % in % (allowed: POSITIVE, NEGATIVE)', p_rh_factor, p_context;
    end if;
end;
$$;

-- Триггер-функция для валидации группы крови донора.
-- Для ACTIVE-доноров blood_group и rh_factor обязательны.
create or replace function trg_validate_donor_profile_blood_fields()
returns trigger
language plpgsql
as $$
begin
    perform fn_validate_blood_fields(
        new.blood_group,
        new.rh_factor,
        upper(new.donor_status) = 'ACTIVE',
        'donor_profile'
    );
    return new;
end;
$$;

-- Триггер вызывается при INSERT / UPDATE donor_profile
-- и предотвращает сохранение некорректных данных
create trigger trg_donor_profile_blood_fields
before insert or update on donor_profile
for each row execute function trg_validate_donor_profile_blood_fields();

-- Триггер-функция для валидации группы крови компонента крови.
-- Поля не обязательны, но если указаны — должны быть валидны.
create or replace function trg_validate_blood_unit_fields()
returns trigger
language plpgsql
as $$
begin
    perform fn_validate_blood_fields(
        new.blood_group,
        new.rh_factor,
        false,
        'blood_unit'
    );
    return new;
end;
$$;

create trigger trg_blood_unit_blood_fields
before insert or update on blood_unit
for each row execute function trg_validate_blood_unit_fields();

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

-- GOD account is created by Spring bootstrap to avoid pgcrypto dependency.

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
