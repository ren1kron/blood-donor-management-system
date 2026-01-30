package ifmo.se.coursach_back.medical.application.command;

import java.util.UUID;

/**
 * Command for updating donor status.
 */
public record UpdateDonorStatusCommand(
        UUID donorId,
        String donorStatus
) {
}
