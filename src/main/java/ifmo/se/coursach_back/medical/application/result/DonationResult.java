package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for donation operations.
 */
public record DonationResult(
        UUID donationId,
        UUID visitId,
        String donationType,
        Integer volumeMl,
        boolean published,
        OffsetDateTime performedAt,
        OffsetDateTime publishedAt
) {
}
