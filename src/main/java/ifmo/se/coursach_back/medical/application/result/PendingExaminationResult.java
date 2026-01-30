package ifmo.se.coursach_back.medical.application.result;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for pending examination.
 */
public record PendingExaminationResult(
        UUID checkId,
        UUID visitId,
        UUID donorId,
        String donorFullName,
        OffsetDateTime submittedAt,
        BigDecimal hemoglobinGl,
        BigDecimal hematocritPct,
        BigDecimal rbc10e12L,
        LabResultInfo labResult
) {
    /**
     * Lab result information.
     */
    public record LabResultInfo(
            UUID labRequestId,
            String status,
            OffsetDateTime completedAt,
            String notes
    ) {
    }
}
