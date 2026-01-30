package ifmo.se.coursach_back.medical.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for sample registration.
 */
public record SampleResult(
        UUID sampleId,
        UUID donationId,
        String sampleCode,
        String status,
        OffsetDateTime collectedAt
) {
}
