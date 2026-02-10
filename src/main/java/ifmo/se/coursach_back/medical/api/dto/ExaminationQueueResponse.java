package ifmo.se.coursach_back.medical.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ifmo.se.coursach_back.donor.domain.BloodGroup;
import ifmo.se.coursach_back.donor.domain.RhFactor;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.medical.domain.Questionnaire;
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
        BigDecimal hematocritPct,
        BigDecimal rbc10e12L,
        BloodGroup bloodGroup,
        RhFactor rhFactor,
        MedicalCheckDecision decision,
        OffsetDateTime decisionAt,
        Boolean questionnaireHasFever,
        Boolean questionnaireTookAntibiotics,
        Boolean questionnaireHasChronicDiseases,
        String questionnaireComment
) {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ExaminationQueueResponse from(Visit visit, LabExaminationRequest request,
                                                 MedicalCheck check, Questionnaire questionnaire) {
        Boolean hasFever = null;
        Boolean tookAntibiotics = null;
        Boolean hasChronicDiseases = null;
        String comment = null;

        if (questionnaire != null && questionnaire.getPayloadJson() != null) {
            try {
                JsonNode json = OBJECT_MAPPER.readTree(questionnaire.getPayloadJson());
                hasFever = json.has("hasFever") ? json.get("hasFever").asBoolean() : null;
                tookAntibiotics = json.has("tookAntibioticsLast14Days") ? json.get("tookAntibioticsLast14Days").asBoolean() : null;
                hasChronicDiseases = json.has("hasChronicDiseases") ? json.get("hasChronicDiseases").asBoolean() : null;
                comment = json.has("comment") && !json.get("comment").isNull() ? json.get("comment").asText() : null;
            } catch (Exception ignored) {
                // If payload parsing fails, leave fields as null
            }
        }

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
                request != null ? request.getHematocritPct() : null,
                request != null ? request.getRbc10e12L() : null,
                request != null ? request.getBloodGroup() : null,
                request != null ? request.getRhFactor() : null,
                check != null ? check.getDecision() : null,
                check != null ? check.getDecisionAt() : null,
                hasFever,
                tookAntibiotics,
                hasChronicDiseases,
                comment
        );
    }
}
