package ifmo.se.coursach_back.nurse.application.command;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import java.util.UUID;

/**
 * Command object for starting a collection session.
 */
public record StartCollectionSessionCommand(
        UUID accountId,
        UUID sessionId,
        VitalsPayload vitals,
        String notes
) {
}
