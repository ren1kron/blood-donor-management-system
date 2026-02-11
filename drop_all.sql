-- =========================
-- 1) Views
-- =========================
drop view if exists v_blood_unit;

-- =========================
-- 2) Triggers
-- =========================
drop trigger if exists trg_report_request_set_updated_at on report_request;
drop trigger if exists trg_collection_session_set_updated_at on collection_session;
drop trigger if exists trg_booking_consistency on booking;
drop trigger if exists trg_donation_publication_consistency on donation;
drop trigger if exists trg_lab_test_result_publication_consistency on lab_test_result;
drop trigger if exists trg_notification_delivery_consistency on notification_delivery;
drop trigger if exists trg_sample_consistency on sample;
drop trigger if exists trg_blood_unit_consistency on blood_unit;

-- =========================
-- 3) Functions
-- =========================
drop function if exists fn_exists_active_booking(uuid, uuid);
drop function if exists fn_count_active_bookings_by_slot(uuid);
drop function if exists fn_get_pending_booking_id(uuid, uuid, text);
drop function if exists fn_get_active_deferral_id(uuid, timestamptz);
drop function if exists fn_get_latest_medical_check_id(uuid);

drop function if exists trg_blood_unit_consistency();
drop function if exists trg_sample_consistency();
drop function if exists trg_notification_delivery_consistency();
drop function if exists trg_publication_consistency();
drop function if exists trg_booking_consistency();
drop function if exists trg_set_updated_at();
drop function if exists normalize_upper_text(text);

-- =========================
-- 4) Indexes
-- =========================
drop index if exists idx_contraindication_name;

drop index if exists idx_account_role_role;

drop index if exists idx_donor_contraindication_donor;
drop index if exists idx_donor_contraindication_contra;

drop index if exists idx_slot_start_at;

drop index if exists uq_booking_donor_slot_active;
drop index if exists idx_booking_donor;
drop index if exists idx_booking_slot;

drop index if exists idx_consent_visit;
drop index if exists idx_consent_donor;

drop index if exists idx_questionnaire_visit;
drop index if exists idx_questionnaire_donor;

drop index if exists idx_medical_check_staff;
drop index if exists idx_medical_check_status;
drop index if exists idx_medical_check_lab;

drop index if exists idx_lab_exam_request_status;
drop index if exists idx_lab_exam_request_requested_at;
drop index if exists idx_lab_exam_request_requested_by;
drop index if exists idx_lab_exam_request_completed_by;
drop index if exists idx_lab_exam_request_hemoglobin;

drop index if exists idx_deferral_donor;

drop index if exists idx_donation_staff;
drop index if exists idx_donation_published;

drop index if exists idx_collection_session_status;
drop index if exists idx_collection_session_nurse;

drop index if exists idx_reaction_donation;

drop index if exists idx_sample_donation;

drop index if exists idx_lab_result_sample;
drop index if exists idx_lab_result_type;
drop index if exists idx_lab_result_staff;

drop index if exists idx_blood_unit_donation;
drop index if exists idx_blood_unit_component;
drop index if exists idx_blood_unit_status;
drop index if exists idx_blood_unit_expires_at;

drop index if exists idx_donor_document_donor;
drop index if exists idx_donor_document_expires;

drop index if exists idx_report_request_status;
drop index if exists idx_report_request_donor;
drop index if exists idx_report_request_requested_by;
drop index if exists idx_report_request_assigned_admin;

drop index if exists idx_delivery_notification;
drop index if exists idx_delivery_donor;
drop index if exists idx_delivery_status;
drop index if exists idx_notification_delivery_staff;

drop index if exists idx_audit_event_account;
drop index if exists idx_audit_event_entity;

drop index if exists idx_booking_slot_status_active;
drop index if exists idx_deferral_active_lookup;

-- =========================
-- 5) Tables (children → parents)
-- =========================
drop table if exists audit_event;

drop table if exists notification_delivery;
drop table if exists notification;

drop table if exists report_request;

drop table if exists donor_document;

drop table if exists blood_unit;

drop table if exists lab_test_result;
drop table if exists sample;

drop table if exists adverse_reaction;

drop table if exists collection_session;

drop table if exists donation;

drop table if exists deferral;

drop table if exists lab_examination_request;

drop table if exists medical_check;

drop table if exists questionnaire;

drop table if exists consent;

drop table if exists visit;

drop table if exists booking;

drop table if exists appointment_slot;

drop table if exists donor_contraindication;

drop table if exists staff_profile;
drop table if exists donor_profile;

drop table if exists account_role;

drop table if exists account;

drop table if exists contraindication;

drop table if exists blood_component_type;
drop table if exists lab_test_type;
drop table if exists role;
