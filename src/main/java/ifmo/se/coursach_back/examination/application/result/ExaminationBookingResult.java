package ifmo.se.coursach_back.examination.application.result;

import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object containing examination booking information.
 */
public record ExaminationBookingResult(
        UUID bookingId,
        BookingStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        SlotInfo slotInfo,
        UUID visitId
) {
    /**
     * Information about the slot associated with the booking.
     */
    public record SlotInfo(
            UUID slotId,
            OffsetDateTime startAt,
            OffsetDateTime endAt,
            String location
    ) {
    }
}
