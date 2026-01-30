package ifmo.se.coursach_back.lab.application.result;

import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for lab examination submission.
 */
public record LabExaminationSubmitResult(
        UUID requestId,
        LabExaminationStatus status,
        OffsetDateTime completedAt
) {
}
