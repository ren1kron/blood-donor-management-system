ALTER TABLE donor_profile
    ALTER COLUMN donor_status SET DEFAULT 'POTENTIAL';

UPDATE donor_profile
SET donor_status = 'POTENTIAL'
WHERE donor_status IS NULL OR btrim(donor_status) = '';
