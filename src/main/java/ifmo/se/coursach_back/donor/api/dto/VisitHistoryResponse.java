package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record VisitHistoryResponse(
        UUID visitId,
        UUID bookingId,
        OffsetDateTime visitDate,
        MedicalCheckDecision medicalDecision,
        OffsetDateTime decisionAt,
        BigDecimal hemoglobinGl,
        BigDecimal hematocritPct,
        BigDecimal rbc10e12L,
        boolean hasDonation
) {
    public static VisitHistoryResponse from(MedicalCheck check, boolean hasDonation) {
        return new VisitHistoryResponse(
                check.getVisit().getId(),
                check.getVisit().getBooking().getId(),
                check.getVisit().getCheckInAt(),
                check.getDecision(),
                check.getDecisionAt(),
                check.getHemoglobinGl(),
                check.getHematocritPct(),
                check.getRbc10e12L(),
                hasDonation
        );
    }
}
