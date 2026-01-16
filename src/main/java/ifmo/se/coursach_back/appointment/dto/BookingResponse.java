package ifmo.se.coursach_back.appointment.dto;

import ifmo.se.coursach_back.model.Booking;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID slotId,
        String status,
        OffsetDateTime createdAt
) {
    public static BookingResponse from(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getSlot().getId(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
