package ifmo.se.coursach_back.lab.dto;

import ifmo.se.coursach_back.model.LabExaminationRequest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LabExaminationResponse(
        UUID requestId,
        UUID visitId,
        UUID bookingId,
        String donorFullName,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String location,
        String status,
        String requestedByName,
        OffsetDateTime requestedAt,
        String completedByName,
        OffsetDateTime completedAt,
        BigDecimal hemoglobinGl,
        BigDecimal hematocritPct,
        BigDecimal rbc10e12L
) {
    public static LabExaminationResponse from(LabExaminationRequest request) {
        return new LabExaminationResponse(
                request.getId(),
                request.getVisit().getId(),
                request.getVisit().getBooking().getId(),
                request.getVisit().getBooking().getDonor().getFullName(),
                request.getVisit().getBooking().getSlot().getStartAt(),
                request.getVisit().getBooking().getSlot().getEndAt(),
                request.getVisit().getBooking().getSlot().getLocation(),
                request.getStatus(),
                request.getRequestedBy() != null ? request.getRequestedBy().getFullName() : null,
                request.getRequestedAt(),
                request.getCompletedByLab() != null ? request.getCompletedByLab().getFullName() : null,
                request.getCompletedAt(),
                request.getHemoglobinGl(),
                request.getHematocritPct(),
                request.getRbc10e12L()
        );
    }
}
