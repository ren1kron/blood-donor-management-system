package ifmo.se.coursach_back.donor.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for consent submission.
 */
public record ConsentResult(
        UUID id,
        UUID visitId,
        UUID donorId,
        String consentType,
        OffsetDateTime givenAt
) {
}
