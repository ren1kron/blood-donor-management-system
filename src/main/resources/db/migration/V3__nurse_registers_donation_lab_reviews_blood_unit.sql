-- V3: Nurse registers donation details during collection session.
-- Lab reviews blood units after donation.

-- Add donation details to collection_session (nurse records these)
ALTER TABLE collection_session
    ADD COLUMN donation_type TEXT,
    ADD COLUMN volume_ml     INTEGER,
    ADD COLUMN donor_state   TEXT;

-- Make component_type_id nullable so blood units can be created before lab review
ALTER TABLE blood_unit
    ALTER COLUMN component_type_id DROP NOT NULL;

-- Add quarantine_reason to blood_unit
ALTER TABLE blood_unit
    ADD COLUMN quarantine_reason TEXT;
