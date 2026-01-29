package ifmo.se.coursach_back.nurse.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CollectionSessionResponse(
        UUID id,
        UUID visitId,
        UUID nurseId,
        String nurseName,
        String status,
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
