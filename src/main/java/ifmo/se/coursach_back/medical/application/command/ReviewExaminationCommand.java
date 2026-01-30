package ifmo.se.coursach_back.medical.application.command;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Command for reviewing a pending examination.
 */
public record ReviewExaminationCommand(
        UUID accountId,
        UUID examinationId,
        String decision,
        DeferralInfo deferral
) {
    /**
     * Deferral information for refused examinations.
     */
    public record DeferralInfo(
            String deferralType,
            String reason,
            OffsetDateTime endsAt
    ) {
    }
}
