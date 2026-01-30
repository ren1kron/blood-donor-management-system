package ifmo.se.coursach_back.nurse.application.command;

import java.util.UUID;

/**
 * Command object for aborting a collection session.
 */
public record AbortCollectionSessionCommand(
        UUID accountId,
        UUID sessionId,
        String reason,
        String notes
) {
}
