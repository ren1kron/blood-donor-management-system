package ifmo.se.coursach_back.examination.dto;

import ifmo.se.coursach_back.model.Booking;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExaminationBookingResponse(
        UUID bookingId,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime expiresAt,
        UUID slotId,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String location
) {
    private static final int BOOKING_TTL_MINUTES = 15;
    
    public static ExaminationBookingResponse from(Booking booking) {
        OffsetDateTime expires = booking.getCreatedAt().plusMinutes(BOOKING_TTL_MINUTES);
        return new ExaminationBookingResponse(
                booking.getId(),
                booking.getStatus(),
                booking.getCreatedAt(),
                expires,
                booking.getSlot().getId(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation()
        );
    }
}
