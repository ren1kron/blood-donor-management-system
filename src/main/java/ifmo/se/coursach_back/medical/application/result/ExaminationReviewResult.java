package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for examination review.
 */
public record ExaminationReviewResult(
        UUID checkId,
        String decision,
        OffsetDateTime decisionAt,
        MedicalCheckResult.DeferralInfo deferralInfo
) {
}
