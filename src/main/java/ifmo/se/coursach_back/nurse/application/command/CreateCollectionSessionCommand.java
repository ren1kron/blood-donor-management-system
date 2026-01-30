package ifmo.se.coursach_back.nurse.application.command;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
import java.util.UUID;

/**
 * Command object for creating a new collection session.
 */
public record CreateCollectionSessionCommand(
        UUID accountId,
        UUID visitId,
        UUID bookingId,
        VitalsPayload preVitals,
        String notes
) {
}
