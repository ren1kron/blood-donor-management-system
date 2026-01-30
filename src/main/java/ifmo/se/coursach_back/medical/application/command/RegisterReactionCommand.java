package ifmo.se.coursach_back.medical.application.command;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Command for registering an adverse reaction.
 */
public record RegisterReactionCommand(
        UUID accountId,
        UUID donationId,
        OffsetDateTime occurredAt,
        String severity,
        String description
) {
}
