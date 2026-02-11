-- Test data seed for BDMS schema
-- Assumes the schema from your script is already applied (tables, funcs, triggers, view).
-- Safe-ish to rerun: uses ON CONFLICT where possible; other rows are "fresh" each run.
-- If you need fully idempotent seeding, tell me what you consider a natural key for each entity.

begin;

-- A couple of contraindications
insert into contraindication (id, name, is_permanent, description)
values
  (gen_random_uuid(), 'Recent tattoo (< 6 months)', false, 'Temporary deferral after tattooing.'),
  (gen_random_uuid(), 'Hepatitis history', true, 'Permanent contraindication.')
on conflict do nothing;

-- Seed a small realistic flow:
-- Accounts -> Profiles -> Slots -> Bookings -> Visit -> Consent/Questionnaire -> Checks/Lab -> Deferral/Donation -> Samples/Results -> BloodUnit -> Notifications/Reports/Audit
with
-- 1) Create accounts
acc as (
  insert into account (email, phone, last_name, first_name, middle_name, password_hash)
  values
    ('donor1@example.com', '+1555000001', 'Ivanov', 'Petr', 'A.', 'hash_donor1'),
    ('doc1@example.com',   '+1555000002', 'Sidorov', 'Ilya', 'B.', 'hash_doc1'),
    ('nurse1@example.com', '+1555000003', 'Smirnova', 'Anna', 'C.', 'hash_nurse1'),
    ('lab1@example.com',   '+1555000004', 'Kuznetsov', 'Denis', 'D.', 'hash_lab1'),
    ('admin1@example.com', '+1555000005', 'Petrova', 'Maria', 'F.', 'hash_admin1')
  returning id, email
),
-- 2) Map role ids
role_map as (
  select
    (select id from role where code = 'DONOR') as r_donor,
    (select id from role where code = 'DOCTOR') as r_doctor,
    (select id from role where code = 'NURSE') as r_nurse,
    (select id from role where code = 'LAB')   as r_lab,
    (select id from role where code = 'ADMIN') as r_admin
),
-- 3) Assign roles to accounts
acc_roles as (
  insert into account_role (account_id, role_id)
  select a.id, r.r_donor from acc a, role_map r where a.email='donor1@example.com'
  union all
  select a.id, r.r_doctor from acc a, role_map r where a.email='doc1@example.com'
  union all
  select a.id, r.r_nurse from acc a, role_map r where a.email='nurse1@example.com'
  union all
  select a.id, r.r_lab from acc a, role_map r where a.email='lab1@example.com'
  union all
  select a.id, r.r_admin from acc a, role_map r where a.email='admin1@example.com'
  returning account_id
),
-- 4) Create profiles
donor as (
  insert into donor_profile (account_id, birth_date, blood_group, rh_factor, donor_status)
  select a.id, date '2005-03-21', 'A', '+', 'POTENTIAL'
  from acc a
  where a.email='donor1@example.com'
  returning id as donor_id, account_id
),
staff as (
  insert into staff_profile (account_id, staff_kind)
  select a.id,
         case a.email
           when 'doc1@example.com' then 'DOCTOR'
           when 'nurse1@example.com' then 'NURSE'
           when 'lab1@example.com' then 'LAB'
           when 'admin1@example.com' then 'ADMIN'
         end
  from acc a
  where a.email in ('doc1@example.com','nurse1@example.com','lab1@example.com','admin1@example.com')
  returning id as staff_id, account_id
),
nurse_id as (
  select s.staff_id
  from staff s
  join acc a on a.id = s.account_id
  where a.email = 'nurse1@example.com'
  limit 1
),
doc as (
  select s.staff_id
  from staff s join acc a on a.id = s.account_id
  where a.email='doc1@example.com'
),
nurse as (
  select s.staff_id
  from staff s join acc a on a.id = s.account_id
  where a.email='nurse1@example.com'
),
lab as (
  select s.staff_id
  from staff s join acc a on a.id = s.account_id
  where a.email='lab1@example.com'
),
admin as (
  select s.staff_id
  from staff s join acc a on a.id = s.account_id
  where a.email='admin1@example.com'
),
-- 5) Link a contraindication to donor (active one)
contra as (
  select id as contraindication_id
  from contraindication
  where name = 'Recent tattoo (< 6 months)'
  limit 1
),
donor_contra as (
  insert into donor_contraindication (donor_id, contraindication_id, detected_at, resolved_at)
  select d.donor_id, c.contraindication_id, now() - interval '10 days', null
  from donor d, contra c
  returning donor_id
),
-- 6) Create 2 appointment slots
slots as (
  insert into appointment_slot (purpose, start_at, end_at, location, capacity)
  values
    ('WHOLE_BLOOD', now() + interval '1 day', now() + interval '1 day' + interval '30 minutes', 'Center A', 3),
    ('PLASMA',      now() + interval '3 days', now() + interval '3 days' + interval '45 minutes', 'Center A', 2)
  returning id as slot_id, purpose, start_at
),
slot_whole as (
  select slot_id from slots where purpose='WHOLE_BLOOD' limit 1
),
-- 7) Booking for donor to WHOLE_BLOOD slot
bk as (
  insert into booking (donor_id, slot_id, status)
  select d.donor_id, s.slot_id, 'BOOKED'
  from donor d, slot_whole s
  returning id as booking_id, donor_id, slot_id
),
-- 8) Visit for that booking
vs as (
  insert into visit (booking_id, check_in_at, visit_status)
  select b.booking_id, null, 'SCHEDULED'
  from bk b
  returning id as visit_id, booking_id
),
-- 9) Consent + questionnaire
cns as (
  insert into consent (visit_id, donor_id, consent_type)
  select v.visit_id, d.donor_id, 'GENERAL_CONSENT'
  from vs v, donor d
  returning id as consent_id
),
qst as (
  insert into questionnaire (
    visit_id, donor_id, has_fever, took_antibiotics_last_14d, has_chronic_diseases, comment
  )
  select v.visit_id, d.donor_id, false, false, false, 'Feeling OK'
  from vs v, donor d
  returning id as questionnaire_id
),
-- 10) Medical check + lab examination request for the same visit
mc as (
  insert into medical_check (
    visit_id, performed_by_staff_id, submitted_by_lab_id, submitted_at,
    status, weight_kg, hemoglobin_g_l, hematocrit_pct, rbc_10e12_l,
    systolic_mmhg, diastolic_mmhg, pulse_rate, body_temperature_c,
    decision
  )
  select
    v.visit_id,
    (select staff_id from doc),
    (select staff_id from lab),
    now(),
    'PENDING_REVIEW',
    74.20, 145.0, 42.0, 4.80,
    120, 80, 68, 36.6,
    'ALLOW_DONATION'
  from vs v
  returning id as medical_check_id, visit_id
),
ler as (
  insert into lab_examination_request (
    visit_id, requested_by_staff_id, requested_at, status,
    completed_by_lab_id, completed_at,
    weight_kg, hemoglobin_g_l, hematocrit_pct, rbc_10e12_l,
    systolic_mmhg, diastolic_mmhg, pulse_rate, body_temperature_c,
    blood_group, rh_factor
  )
  select
    v.visit_id,
    (select staff_id from doc),
    now(),
    'REQUESTED',
    (select staff_id from lab),
    now() + interval '1 hour',
    74.20, 145.0, 42.0, 4.80,
    120, 80, 68, 36.6,
    'A', '+'
  from vs v
  returning id as lab_req_id, visit_id
),
-- 11) Donation + collection session
don as (
  insert into donation (
    visit_id, donation_type, volume_ml, performed_by_staff_id, performed_at, is_published
  )
  select
    v.visit_id,
    'WHOLE_BLOOD',
    450,
    (select staff_id from nurse_id),
    now(),
    true
  from vs v
  returning id as donation_id, visit_id
),
cs as (
  insert into collection_session (
    visit_id, nurse_staff_id, status, started_at, ended_at,
    pre_systolic_mmhg, pre_diastolic_mmhg, pre_pulse_rate, pre_body_temperature_c, pre_wellbeing,
    post_systolic_mmhg, post_diastolic_mmhg, post_pulse_rate, post_body_temperature_c, post_wellbeing,
    notes
  )
  select
    v.visit_id,
    (select staff_id from nurse),
    'COMPLETED',
    now() - interval '20 minutes',
    now() - interval '5 minutes',
    120, 80, 70, 36.6, 'GOOD',
    118, 78, 72, 36.7, 'GOOD',
    'No issues'
  from vs v
  returning id as collection_session_id
),
-- 12) Sample + test results
smp as (
  insert into sample (donation_id, sample_code, status, quarantine_reason, rejection_reason)
  select d.donation_id, 'SMP-' || to_char(extract(epoch from now())::bigint, 'FM999999999999999'),
         'NEW', null, null
  from don d
  returning id as sample_id, donation_id
),
tt as (
  select
    (select id from lab_test_type where code='HIV') as t_hiv,
    (select id from lab_test_type where code='HBSAG') as t_hbsag,
    (select id from lab_test_type where code='HCV') as t_hcv
),
ltr as (
  insert into lab_test_result (
    sample_id, test_type_id, labtech_staff_id,
    result_value, result_flag, tested_at, is_published
  )
  select s.sample_id, t.t_hiv, (select staff_id from lab), 'NEGATIVE', 'OK', now(), true
  from smp s, tt t
  union all
  select s.sample_id, t.t_hbsag, (select staff_id from lab), 'NEGATIVE', 'OK', now(), true
  from smp s, tt t
  union all
  select s.sample_id, t.t_hcv, (select staff_id from lab), 'NEGATIVE', 'OK', now(), true
  from smp s, tt t
  returning id as lab_test_result_id
),
-- 13) Blood unit from donation (component RBC)
bct as (
  select id as component_type_id
  from blood_component_type
  where code='RBC'
  limit 1
),
bu as (
  insert into blood_unit (
    donation_id, component_type_id, blood_group, rh_factor, volume_ml,
    collected_at, expires_at, status, storage_location
  )
  select
    d.donation_id,
    b.component_type_id,
    'A', '+',
    280,
    now(),
    now() + interval '35 days',
    'IN_STOCK',
    'Fridge A-1'
  from don d, bct b
  returning id as blood_unit_id
),
-- 14) Donor document
docu as (
  insert into donor_document (donor_id, doc_type, issued_at, expires_at, status)
  select d.donor_id, 'PASSPORT', date '2021-06-01', date '2031-06-01', 'VALID'
  from donor d
  returning id as donor_document_id
),
-- 15) Report request (by admin) + notification
rr as (
  insert into report_request (
    donor_id, requested_by_staff_id, requested_by_role,
    assigned_admin_id, report_type, status, payload_text, message
  )
  select
    d.donor_id,
    (select staff_id from admin),
    'ADMIN',
    (select staff_id from admin),
    'DONATION_HISTORY',
    'REQUESTED',
    '{"period":"last_12_months"}',
    'Generate donor report for the last 12 months'
  from donor d
  returning id as report_request_id
),
ntf as (
  insert into notification (channel, topic, body)
  values ('EMAIL', 'DONATION_COMPLETED', 'Your donation has been recorded. Thank you!')
  returning id as notification_id
),
nd as (
  insert into notification_delivery (notification_id, donor_id, staff_id, status)
  select n.notification_id, d.donor_id, null, 'SENT'
  from ntf n, donor d
  returning id as notification_delivery_id
),
-- 16) Audit event
ae as (
  insert into audit_event (account_id, action, entity_type, entity_id, metadata_text)
  select a.id, 'CREATE', 'DONATION', d.donation_id, '{"source":"seed"}'
  from acc a, don d
  where a.email='nurse1@example.com'
  returning id as audit_event_id
)
select
  'seed_ok' as status,
  (select count(*) from acc) as accounts_created,
  (select count(*) from slots) as slots_created,
  (select count(*) from ltr) as lab_results_created;

commit;
