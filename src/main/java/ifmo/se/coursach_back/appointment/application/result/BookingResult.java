package ifmo.se.coursach_back.appointment.application.result;

import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for booking operations.
 */
public record BookingResult(
        UUID id,
        UUID slotId,
        BookingStatus status,
        OffsetDateTime createdAt
) {
}
