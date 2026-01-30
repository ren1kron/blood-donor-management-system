package ifmo.se.coursach_back.lab.application.result;

import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Result object for lab examination request queries.
 */
public record LabExaminationRequestResult(
        List<LabExaminationRequestItem> items
) {
    /**
     * A single lab examination request item.
     */
    public record LabExaminationRequestItem(
            UUID requestId,
            LabExaminationStatus status,
            UUID visitId,
            UUID bookingId,
            UUID donorId,
            String donorFullName,
            OffsetDateTime slotStartAt,
            OffsetDateTime slotEndAt,
            String location,
            OffsetDateTime requestedAt,
            OffsetDateTime completedAt,
            BigDecimal hemoglobinGl,
            BigDecimal hematocritPct,
            BigDecimal rbc10e12L
    ) {
    }
}
