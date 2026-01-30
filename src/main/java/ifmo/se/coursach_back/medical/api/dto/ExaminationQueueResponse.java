package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.appointment.domain.Visit;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ExaminationQueueResponse(
        UUID visitId,
        UUID bookingId,
        UUID donorId,
        String donorFullName,
        OffsetDateTime slotStartAt,
        OffsetDateTime slotEndAt,
        String location,
        UUID labRequestId,
        LabExaminationStatus labStatus,
        OffsetDateTime labRequestedAt,
        OffsetDateTime labCompletedAt,
        BigDecimal weightKg,
        BigDecimal hemoglobinGl,
        Integer systolicMmhg,
        Integer diastolicMmhg,
        Integer pulseRate,
        BigDecimal bodyTemperatureC,
        MedicalCheckDecision decision,
        OffsetDateTime decisionAt
) {
    public static ExaminationQueueResponse from(Visit visit, LabExaminationRequest request, MedicalCheck check) {
        return new ExaminationQueueResponse(
                visit.getId(),
                visit.getBooking().getId(),
                visit.getBooking().getDonor().getId(),
                visit.getBooking().getDonor().getFullName(),
                visit.getBooking().getSlot().getStartAt(),
                visit.getBooking().getSlot().getEndAt(),
                visit.getBooking().getSlot().getLocation(),
                request != null ? request.getId() : null,
                request != null ? request.getStatus() : null,
                request != null ? request.getRequestedAt() : null,
                request != null ? request.getCompletedAt() : null,
                request != null ? request.getWeightKg() : null,
                request != null ? request.getHemoglobinGl() : null,
                request != null ? request.getSystolicMmhg() : null,
                request != null ? request.getDiastolicMmhg() : null,
                request != null ? request.getPulseRate() : null,
                request != null ? request.getBodyTemperatureC() : null,
                check != null ? check.getDecision() : null,
                check != null ? check.getDecisionAt() : null
        );
    }
}
