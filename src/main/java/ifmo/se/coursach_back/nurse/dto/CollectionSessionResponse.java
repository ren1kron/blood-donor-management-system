package ifmo.se.coursach_back.nurse.dto;

import ifmo.se.coursach_back.model.CollectionSessionStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CollectionSessionResponse(
        UUID id,
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
