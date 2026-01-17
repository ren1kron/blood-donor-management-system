package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.Booking;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonorBookingResponse(
        UUID bookingId,
        UUID slotId,
        String purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime cancelledAt
) {
    public static DonorBookingResponse from(Booking booking) {
        return new DonorBookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getSlot().getPurpose(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getCancelledAt()
        );
    }
}
