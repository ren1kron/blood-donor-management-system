package ifmo.se.coursach_back.medical.application.result;

import java.util.UUID;

/**
 * Result for donor status update.
 */
public record DonorStatusResult(
        UUID donorId,
        String donorStatus
) {
}
