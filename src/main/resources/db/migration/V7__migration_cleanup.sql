-- Migration hygiene: add missing FK indexes and drop redundant ones

-- Redundant indexes (unique constraints already create indexes)
DROP INDEX IF EXISTS idx_lab_exam_request_visit;
DROP INDEX IF EXISTS idx_collection_session_visit;

-- Helpful FK indexes
CREATE INDEX IF NOT EXISTS idx_consent_donor ON consent (donor_id);
CREATE INDEX IF NOT EXISTS idx_questionnaire_donor ON questionnaire (donor_id);
CREATE INDEX IF NOT EXISTS idx_lab_exam_request_requested_by ON lab_examination_request (requested_by_staff_id);
CREATE INDEX IF NOT EXISTS idx_lab_exam_request_completed_by ON lab_examination_request (completed_by_lab_id);
CREATE INDEX IF NOT EXISTS idx_collection_session_nurse ON collection_session (nurse_staff_id);
CREATE INDEX IF NOT EXISTS idx_report_request_assigned_admin ON report_request (assigned_admin_id);
CREATE INDEX IF NOT EXISTS idx_notification_delivery_staff ON notification_delivery (staff_id);
