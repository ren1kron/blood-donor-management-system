package ifmo.se.coursach_back.donor.application.result;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
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
        VitalsPayload preVitals,
        VitalsPayload postVitals,
        boolean hasVitals
) {
}
