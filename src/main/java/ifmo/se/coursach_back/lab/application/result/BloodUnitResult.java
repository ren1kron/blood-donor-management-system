package ifmo.se.coursach_back.lab.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result of blood unit review by lab.
 */
public record BloodUnitResult(
        UUID id,
        UUID donationId,
        UUID visitId,
        String donorFullName,
        String donationType,
        Integer volumeMl,
        String bloodGroup,
        String rhFactor,
        Short componentTypeId,
        String componentTypeCode,
        OffsetDateTime collectedAt,
        OffsetDateTime expiresAt,
        String status,
        String storageLocation,
        String quarantineReason,
        String collectionNotes,
        String collectionComplications,
        String collectionDonorState
) {
}
