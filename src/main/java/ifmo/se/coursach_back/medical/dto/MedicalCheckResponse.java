package ifmo.se.coursach_back.medical.dto;

import ifmo.se.coursach_back.model.Booking;
import ifmo.se.coursach_back.model.Deferral;
import ifmo.se.coursach_back.model.MedicalCheck;
import ifmo.se.coursach_back.model.MedicalCheckDecision;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MedicalCheckResponse(
        UUID id,
        UUID visitId,
        UUID bookingId,
        UUID donorId,
        MedicalCheckDecision decision,
        OffsetDateTime decisionAt,
        UUID deferralId
) {
    public static MedicalCheckResponse from(MedicalCheck check, Booking booking, Deferral deferral) {
        return new MedicalCheckResponse(
                check.getId(),
                check.getVisit().getId(),
                booking.getId(),
                booking.getDonor().getId(),
                check.getDecision(),
                check.getDecisionAt(),
                deferral != null ? deferral.getId() : null
        );
    }
}
