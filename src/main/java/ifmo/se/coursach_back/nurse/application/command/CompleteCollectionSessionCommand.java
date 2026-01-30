package ifmo.se.coursach_back.nurse.application.command;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import java.util.UUID;

/**
 * Command object for completing a collection session.
 */
public record CompleteCollectionSessionCommand(
        UUID accountId,
        UUID sessionId,
        VitalsPayload postVitals,
        String notes
) {
}
