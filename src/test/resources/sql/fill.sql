begin;

-- Fixed IDs for repeatable assertions.
-- Donor account/profile
insert into account (id, email, phone, password_hash, is_active)
values ('11111111-1111-1111-1111-111111111111', 'donor.test@example.com', null, 'hash', true);

insert into donor_profile (id, account_id, full_name, birth_date, donor_status)
values ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Test Donor', '1990-01-01', 'ACTIVE');

-- Staff account/profile (LAB)
insert into account (id, email, phone, password_hash, is_active)
values ('33333333-3333-3333-3333-333333333333', 'lab.test@example.com', null, 'hash', true);

insert into staff_profile (id, account_id, full_name, staff_kind)
values ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333', 'Lab Tech', 'LAB');

-- Booking/visit/donation
insert into appointment_slot (id, purpose, start_at, end_at, location, capacity)
values ('55555555-5555-5555-5555-555555555555', 'DONATION', now() - interval '10 days', now() - interval '10 days' + interval '30 minutes', 'Test Room', 5);

insert into booking (id, donor_id, slot_id, status, created_at)
values ('66666666-6666-6666-6666-666666666666', '22222222-2222-2222-2222-222222222222', '55555555-5555-5555-5555-555555555555', 'BOOKED', now() - interval '11 days');

insert into visit (id, booking_id, check_in_at, visit_status)
values ('77777777-7777-7777-7777-777777777777', '66666666-6666-6666-6666-666666666666', now() - interval '10 days', 'DONE');

insert into donation (id, visit_id, donation_type, volume_ml, performed_by_staff_id, performed_at)
values ('88888888-8888-8888-8888-888888888888', '77777777-7777-7777-7777-777777777777', 'WHOLE_BLOOD', 450, '44444444-4444-4444-4444-444444444444', now() - interval '70 days');

-- Sample + lab result
insert into sample (id, donation_id, sample_code, status, collected_at)
values ('99999999-9999-9999-9999-999999999999', '88888888-8888-8888-8888-888888888888', 'SAMPLE-TEST-001', 'NEW', now() - interval '9 days');

do $$
declare
    v_test_type_id smallint;
begin
    select id into v_test_type_id from lab_test_type where code = 'HIV';
    if v_test_type_id is null then
        raise exception 'lab_test_type HIV not seeded';
    end if;

    insert into lab_test_result (id, sample_id, test_type_id, labtech_staff_id, result_value, result_flag,
                                 tested_at, is_published, published_at)
    values ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '99999999-9999-9999-9999-999999999999', v_test_type_id,
            '44444444-4444-4444-4444-444444444444', 'NEG', 'NEGATIVE', now() - interval '8 days', true,
            now() - interval '7 days');
end $$;

-- Expired document
insert into donor_document (id, donor_id, doc_type, issued_at, expires_at, status)
values ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'PASSPORT',
        current_date - 400, current_date - 1, 'VALID');

-- Active deferral
insert into deferral (id, donor_id, deferral_type, reason, starts_at, ends_at)
values ('cccccccc-cccc-cccc-cccc-cccccccccccc', '22222222-2222-2222-2222-222222222222', 'TEMP',
        'Test deferral', now() - interval '1 day', now() + interval '6 days');

-- Notification delivery
insert into notification (id, channel, topic, body, created_at)
values ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'PHONE', 'REVISIT', 'Test reminder', now() - interval '1 day');

insert into notification_delivery (id, notification_id, donor_id, sent_at, status)
values ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'dddddddd-dddd-dddd-dddd-dddddddddddd',
        '22222222-2222-2222-2222-222222222222', now() - interval '1 day', 'SENT');

commit;