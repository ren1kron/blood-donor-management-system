package ifmo.se.coursach_back.medical.dto;

import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Visit;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ScheduledDonorResponse(
        UUID bookingId,
        UUID visitId,
        UUID donorId,
        String donorFullName,
        String donorStatus,
        UUID slotId,
        String purpose,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        String bookingStatus
) {
    public static ScheduledDonorResponse from(Booking booking, Visit visit) {
        return new ScheduledDonorResponse(
                booking.getId(),
                visit != null ? visit.getId() : null,
                booking.getDonor().getId(),
                booking.getDonor().getFullName(),
                booking.getDonor().getDonorStatus(),
                booking.getSlot().getId(),
                booking.getSlot().getPurpose(),
                booking.getSlot().getStartAt(),
                booking.getSlot().getEndAt(),
                booking.getSlot().getLocation(),
                booking.getStatus()
        );
    }
}
