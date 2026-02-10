-- Critical read-path SQL functions used by booking and eligibility workflows.

create or replace function fn_exists_active_booking(p_donor_id uuid, p_slot_id uuid)
returns boolean
language sql
stable
as $$
    select exists (
        select 1
        from booking b
        where b.donor_id = p_donor_id
          and b.slot_id = p_slot_id
          and upper(trim(b.status)) <> 'CANCELLED'
    )
$$;

create or replace function fn_count_active_bookings_by_slot(p_slot_id uuid)
returns bigint
language sql
stable
as $$
    select count(*)
    from booking b
    where b.slot_id = p_slot_id
      and upper(trim(b.status)) in ('PENDING_QUESTIONNAIRE', 'CONFIRMED', 'BOOKED')
      and b.cancelled_at is null
$$;

create or replace function fn_get_pending_booking_id(
    p_donor_id uuid,
    p_slot_id uuid,
    p_status text
)
returns uuid
language sql
stable
as $$
    select b.id
    from booking b
    where b.donor_id = p_donor_id
      and b.slot_id = p_slot_id
      and upper(trim(b.status)) = upper(trim(p_status))
      and b.cancelled_at is null
    order by b.created_at desc, b.id desc
    limit 1
$$;

create or replace function fn_get_active_deferral_id(p_donor_id uuid, p_now timestamptz)
returns uuid
language sql
stable
as $$
    select d.id
    from deferral d
    where d.donor_id = p_donor_id
      and d.starts_at <= p_now
      and (d.ends_at is null or d.ends_at > p_now)
    order by d.starts_at desc, d.id desc
    limit 1
$$;

create or replace function fn_get_latest_medical_check_id(p_donor_id uuid)
returns uuid
language sql
stable
as $$
    select mc.id
    from medical_check mc
    join visit v on v.id = mc.visit_id
    join booking b on b.id = v.booking_id
    where b.donor_id = p_donor_id
    order by mc.decision_at desc nulls last, mc.submitted_at desc nulls last, mc.id desc
    limit 1
$$;

create index if not exists idx_booking_slot_status_active
    on booking (slot_id, status)
    where cancelled_at is null;

create index if not exists idx_deferral_active_lookup
    on deferral (donor_id, starts_at desc, ends_at);
