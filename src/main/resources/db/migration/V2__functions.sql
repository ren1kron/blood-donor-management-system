-- Stored functions and procedures for core use cases.

create or replace function fn_eligible_donors(p_threshold timestamptz)
returns table (
    donor_id uuid,
    full_name text,
    phone text,
    email text,
    last_donation_at timestamptz
)
language plpgsql
as $$
begin
    return query
    select
        donor.id,
        account.full_name,
        account.phone,
        account.email,
        max(donation.performed_at)
    from donation
    join visit on donation.visit_id = visit.id
    join booking on visit.booking_id = booking.id
    join donor_profile donor on booking.donor_id = donor.id
    join account on donor.account_id = account.id
    group by donor.id, account.full_name, account.phone, account.email
    having max(donation.performed_at) <= p_threshold
    order by max(donation.performed_at) asc;
end;
$$;

create or replace function fn_expired_documents(p_as_of date)
returns table (
    document_id uuid,
    donor_id uuid,
    full_name text,
    phone text,
    email text,
    doc_type text,
    expires_at date
)
language plpgsql
as $$
begin
    return query
    select
        document.id,
        donor.id,
        account.full_name,
        account.phone,
        account.email,
        document.doc_type,
        document.expires_at
    from donor_document document
    join donor_profile donor on document.donor_id = donor.id
    join account on donor.account_id = account.id
    where document.expires_at is not null
      and document.expires_at < p_as_of
    order by document.expires_at asc;
end;
$$;

create or replace function fn_pending_samples(p_statuses text[])
returns table (
    sample_id uuid,
    sample_code text,
    status text,
    collected_at timestamptz,
    donation_id uuid,
    donor_id uuid,
    donor_full_name text
)
language plpgsql
as $$
begin
    return query
    select
        sample.id,
        sample.sample_code,
        sample.status,
        sample.collected_at,
        sample.donation_id,
        donor.id,
        account.full_name
    from sample
    join donation on sample.donation_id = donation.id
    join visit on donation.visit_id = visit.id
    join booking on visit.booking_id = booking.id
    join donor_profile donor on booking.donor_id = donor.id
    join account on donor.account_id = account.id
    where sample.status = any(p_statuses)
    order by sample.collected_at asc;
end;
$$;

create or replace function fn_published_lab_results(p_account_id uuid)
returns table (
    result_id uuid,
    sample_id uuid,
    sample_code text,
    test_type_id smallint,
    test_type_code text,
    result_flag text,
    result_value text,
    tested_at timestamptz,
    published_at timestamptz
)
language plpgsql
as $$
begin
    return query
    select
        result.id,
        result.sample_id,
        sample.sample_code,
        result.test_type_id,
        test_type.code,
        result.result_flag,
        result.result_value,
        result.tested_at,
        result.published_at
    from lab_test_result result
    join sample on result.sample_id = sample.id
    join lab_test_type test_type on result.test_type_id = test_type.id
    join donation on sample.donation_id = donation.id
    join visit on donation.visit_id = visit.id
    join booking on visit.booking_id = booking.id
    join donor_profile donor on booking.donor_id = donor.id
    where donor.account_id = p_account_id
      and result.is_published = true
    order by result.tested_at desc;
end;
$$;

create or replace function fn_donor_donations(p_account_id uuid)
returns table (
    donation_id uuid,
    visit_id uuid,
    performed_at timestamptz,
    donation_type text,
    volume_ml integer
)
language plpgsql
as $$
begin
    return query
    select
        donation.id,
        donation.visit_id,
        donation.performed_at,
        donation.donation_type,
        donation.volume_ml
    from donation
    join visit on donation.visit_id = visit.id
    join booking on visit.booking_id = booking.id
    join donor_profile donor on booking.donor_id = donor.id
    where donor.account_id = p_account_id
    order by donation.performed_at desc;
end;
$$;

create or replace function fn_active_deferral(p_donor_id uuid, p_now timestamptz)
returns table (
    deferral_id uuid,
    deferral_type text,
    reason text,
    starts_at timestamptz,
    ends_at timestamptz
)
language plpgsql
as $$
begin
    return query
    select
        deferral.id,
        deferral.deferral_type,
        deferral.reason,
        deferral.starts_at,
        deferral.ends_at
    from deferral
    where deferral.donor_id = p_donor_id
      and (deferral.ends_at is null or deferral.ends_at > p_now)
    order by deferral.starts_at desc
    limit 1;
end;
$$;

create or replace procedure sp_ack_notification(p_delivery_id uuid, p_donor_id uuid)
language plpgsql
as $$
begin
    update notification_delivery
    set status = 'ACKED'
    where id = p_delivery_id
      and donor_id = p_donor_id;
end;
$$;
