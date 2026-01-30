package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for adverse reaction registration.
 */
public record ReactionResult(
        UUID reactionId,
        UUID donationId,
        String severity,
        String description,
        OffsetDateTime occurredAt
) {
}
