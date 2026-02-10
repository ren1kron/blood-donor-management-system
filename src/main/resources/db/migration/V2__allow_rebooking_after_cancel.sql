-- Drop the unconditional unique constraint on (donor_id, slot_id)
-- so that a donor can rebook the same slot after cancelling.
ALTER TABLE booking DROP CONSTRAINT uq_booking_donor_slot;

-- Create a partial unique index that only prevents duplicate *active* bookings
-- (i.e. those that haven't been cancelled).
CREATE UNIQUE INDEX uq_booking_donor_slot_active
    ON booking (donor_id, slot_id)
    WHERE cancelled_at IS NULL;
