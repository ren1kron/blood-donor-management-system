package ifmo.se.coursach_back.lab.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for blood unit information shown to lab.
 */
public record BloodUnitResponse(
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
