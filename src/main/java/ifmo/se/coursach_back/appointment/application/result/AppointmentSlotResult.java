package ifmo.se.coursach_back.appointment.application.result;

import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for appointment slot operations.
 */
public record AppointmentSlotResult(
        UUID id,
        SlotPurpose purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        int capacity,
        long bookedCount
) {
}
