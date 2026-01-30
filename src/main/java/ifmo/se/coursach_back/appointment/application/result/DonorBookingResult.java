package ifmo.se.coursach_back.appointment.application.result;

import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for donor booking listing.
 */
public record DonorBookingResult(
        UUID id,
        UUID slotId,
        SlotPurpose slotPurpose,
        OffsetDateTime slotStartTime,
        OffsetDateTime slotEndTime,
        String slotLocation,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime cancelledAt,
        boolean hasVisit
) {
}
