package ifmo.se.coursach_back.lab.application.command;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Command for lab to review a blood unit.
 */
public record ReviewBloodUnitCommand(
        UUID accountId,
        UUID bloodUnitId,
        OffsetDateTime expiresAt,
        String status,
        String storageLocation,
        String quarantineReason
) {
}
