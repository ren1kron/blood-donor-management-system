-- ===============================
-- DROP SCHEMA: Blood Donation Management System
-- ===============================

-- Drop dependent tables first (reverse order of creation)

drop table if exists notification_delivery cascade;
drop table if exists notification cascade;

drop table if exists donor_document cascade;

drop table if exists blood_unit cascade;
drop table if exists lab_test_result cascade;
drop table if exists sample cascade;
drop table if exists adverse_reaction cascade;
drop table if exists donation cascade;

drop table if exists deferral cascade;
drop table if exists medical_check cascade;
drop table if exists questionnaire cascade;
drop table if exists consent cascade;
drop table if exists visit cascade;

drop table if exists booking cascade;
drop table if exists appointment_slot cascade;

drop table if exists donor_contraindication cascade;
drop table if exists contraindication cascade;

drop table if exists staff_profile cascade;
drop table if exists donor_profile cascade;

drop table if exists account_role cascade;
drop table if exists account cascade;

drop table if exists lab_test_type cascade;
drop table if exists blood_component_type cascade;
drop table if exists role cascade;

-- ===============================
-- END DROP SCRIPT
-- ===============================
