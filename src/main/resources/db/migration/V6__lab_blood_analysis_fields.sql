-- Part A: LAB only sends blood analysis results (HB, HCT, RBC)
-- Remove vitals-related fields from lab_examination_request as they are handled by nurse

-- Add new blood analysis fields to lab_examination_request
ALTER TABLE lab_examination_request
ADD COLUMN hematocrit_pct numeric(5,2),
ADD COLUMN rbc_10e12_l numeric(5,2);

-- Add new blood analysis fields to medical_check
ALTER TABLE medical_check
ADD COLUMN hematocrit_pct numeric(5,2),
ADD COLUMN rbc_10e12_l numeric(5,2);

-- Create indexes for efficient querying
CREATE INDEX idx_lab_exam_request_hemoglobin ON lab_examination_request (hemoglobin_g_l);
