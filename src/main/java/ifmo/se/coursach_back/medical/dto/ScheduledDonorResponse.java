package ifmo.se.coursach_back.medical.dto;

import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.MedicalCheck;
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
        String bookingStatus,
        String medicalDecision,
        boolean hasDonation,
        boolean canDonate
) {
    public static ScheduledDonorResponse from(Booking booking, Visit visit, MedicalCheck check, boolean hasDonation) {
        String decision = check != null ? check.getDecision() : null;
        boolean canDonate = "ADMITTED".equals(decision) && !hasDonation;
        
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
                booking.getStatus(),
                decision,
                hasDonation,
                canDonate
        );
    }
    
    // Backwards compatible factory method
    public static ScheduledDonorResponse from(Booking booking, Visit visit) {
        return from(booking, visit, null, false);
    }
}
