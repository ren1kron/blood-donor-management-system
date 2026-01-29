package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.Booking;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonorBookingResponse(
        UUID id,
        UUID slotId,
        String slotPurpose,
        OffsetDateTime slotStartTime,
        OffsetDateTime slotEndTime,
        String slotLocation,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime cancelledAt,
        boolean hasVisit
) {
    public static DonorBookingResponse from(Booking booking, boolean hasVisit) {
        return new DonorBookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getSlot().getPurpose(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt(),
                hasVisit
        );
    }
}
