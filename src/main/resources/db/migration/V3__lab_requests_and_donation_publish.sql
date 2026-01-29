-- Lab examination requests (doctor -> lab)
CREATE TABLE lab_examination_request (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id uuid NOT NULL UNIQUE REFERENCES visit(id) ON DELETE CASCADE,
    requested_by_staff_id uuid NOT NULL REFERENCES staff_profile(id) ON DELETE RESTRICT,
    requested_at timestamptz NOT NULL DEFAULT now(),
    status text NOT NULL DEFAULT 'REQUESTED',
    completed_by_lab_id uuid REFERENCES staff_profile(id) ON DELETE SET NULL,
    completed_at timestamptz,
    weight_kg numeric(5,2),
    hemoglobin_g_l numeric(6,2),
    systolic_mmhg integer,
    diastolic_mmhg integer,
    pulse_rate integer,
    body_temperature_c numeric(4,2)
);

CREATE INDEX idx_lab_exam_request_visit ON lab_examination_request (visit_id);
CREATE INDEX idx_lab_exam_request_status ON lab_examination_request (status);
CREATE INDEX idx_lab_exam_request_requested_at ON lab_examination_request (requested_at);

-- Donation publish fields
ALTER TABLE donation
    ADD COLUMN is_published boolean NOT NULL DEFAULT false,
    ADD COLUMN published_at timestamptz;

CREATE INDEX idx_donation_published ON donation (is_published);

-- Backfill existing donations as published to preserve visibility
UPDATE donation
SET is_published = true,
    published_at = COALESCE(published_at, performed_at)
WHERE is_published = false;
