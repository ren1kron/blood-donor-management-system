package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PendingExaminationResponse(
        UUID id,
        UUID visitId,
        UUID bookingId,
        String donorFullName,
        String donorBloodGroup,
        String donorRhFactor,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC,
        MedicalCheckDecision status,
        String submittedByLabName,
        OffsetDateTime submittedAt
) {
    public static PendingExaminationResponse from(MedicalCheck check) {
        var donor = check.getVisit().getBooking().getDonor();
        return new PendingExaminationResponse(
                check.getId(),
                check.getVisit().getId(),
                check.getVisit().getBooking().getId(),
                donor.getFullName(),
                donor.getBloodGroup() != null ? donor.getBloodGroup().getDisplayValue() : null,
                donor.getRhFactor() != null ? donor.getRhFactor().getDisplayValue() : null,
                check.getWeightKg(),
                check.getHemoglobinGl(),
                check.getSystolicMmhg(),
                check.getDiastolicMmhg(),
                check.getPulseRate(),
                check.getBodyTemperatureC(),
                check.getStatus(),
                check.getSubmittedByLab() != null ? check.getSubmittedByLab().getFullName() : null,
                check.getSubmittedAt()
        );
    }
}
