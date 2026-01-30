package ifmo.se.coursach_back.donor.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for donation history.
 */
public record DonationHistoryResult(
        UUID donationId,
        UUID visitId,
        OffsetDateTime performedAt,
        String donationType,
        Integer volumeMl,
        OffsetDateTime publishedAt,
        String preVitalsJson,
        String postVitalsJson,
        boolean hasVitals
) {
}
