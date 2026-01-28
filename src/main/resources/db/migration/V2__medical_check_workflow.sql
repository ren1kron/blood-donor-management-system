ALTER TABLE medical_check 
ADD COLUMN submitted_by_lab_id uuid REFERENCES staff_profile(id),
ADD COLUMN submitted_at timestamptz,
ADD COLUMN status text NOT NULL DEFAULT 'PENDING_REVIEW';

ALTER TABLE medical_check 
ALTER COLUMN performed_by_staff_id DROP NOT NULL;

ALTER TABLE medical_check
ADD COLUMN pulse_rate integer,
ADD COLUMN body_temperature_c numeric(4,2);

CREATE INDEX idx_medical_check_status ON medical_check (status);
CREATE INDEX idx_medical_check_lab ON medical_check (submitted_by_lab_id);

UPDATE medical_check SET status = decision WHERE decision IN ('ADMITTED', 'REFUSED');
