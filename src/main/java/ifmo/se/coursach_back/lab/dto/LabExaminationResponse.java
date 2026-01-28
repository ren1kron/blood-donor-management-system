package ifmo.se.coursach_back.lab.dto;

import ifmo.se.coursach_back.model.MedicalCheck;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LabExaminationResponse(
        UUID id,
        UUID visitId,
        UUID bookingId,
        String donorFullName,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC,
        String status,
        String submittedByLabName,
        OffsetDateTime submittedAt,
        String reviewedByDoctorName,
        String decision,
        OffsetDateTime decisionAt
) {
    public static LabExaminationResponse from(MedicalCheck check) {
        return new LabExaminationResponse(
                check.getId(),
                check.getVisit().getId(),
                check.getVisit().getBooking().getId(),
                check.getVisit().getBooking().getDonor().getFullName(),
                check.getWeightKg(),
                check.getHemoglobinGl(),
                check.getSystolicMmhg(),
                check.getDiastolicMmhg(),
                check.getPulseRate(),
                check.getBodyTemperatureC(),
                check.getStatus(),
                check.getSubmittedByLab() != null ? check.getSubmittedByLab().getFullName() : null,
                check.getSubmittedAt(),
                check.getPerformedBy() != null ? check.getPerformedBy().getFullName() : null,
                check.getDecision(),
                check.getDecisionAt()
        );
    }
}
