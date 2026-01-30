package ifmo.se.coursach_back.examination.api.dto;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.appointment.domain.BookingStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExaminationBookingResponse(
        UUID bookingId,
        BookingStatus status,
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
