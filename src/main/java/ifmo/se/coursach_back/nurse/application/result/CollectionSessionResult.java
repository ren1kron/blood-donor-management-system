package ifmo.se.coursach_back.nurse.application.result;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import ifmo.se.coursach_back.nurse.domain.CollectionSessionStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for collection session operations.
 */
public record CollectionSessionResult(
        UUID sessionId,
        UUID visitId,
        UUID nurseId,
        String nurseName,
        CollectionSessionStatus status,
        OffsetDateTime startedAt,
        OffsetDateTime endedAt,
        VitalsPayload preVitals,
        VitalsPayload postVitals,
        String notes,
        String complications,
        String interruptionReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
