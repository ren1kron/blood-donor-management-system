package ifmo.se.coursach_back.examination.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object containing examination slot information.
 */
public record ExaminationSlotResult(
        UUID slotId,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String location,
        int capacity,
        int availableCapacity
) {
}
