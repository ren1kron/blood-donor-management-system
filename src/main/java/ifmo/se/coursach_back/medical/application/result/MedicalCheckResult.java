package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for medical check operations.
 */
public record MedicalCheckResult(
        UUID checkId,
        String decision,
        OffsetDateTime decisionAt,
        DeferralInfo deferralInfo
) {
    /**
     * Deferral information included in the result.
     */
    public record DeferralInfo(
            UUID deferralId,
            String deferralType,
            String reason,
            OffsetDateTime endsAt
    ) {
    }
}
