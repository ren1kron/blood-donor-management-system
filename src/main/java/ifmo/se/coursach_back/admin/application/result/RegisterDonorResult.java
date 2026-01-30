package ifmo.se.coursach_back.admin.application.result;

import java.util.UUID;

/**
 * Result object for donor registration.
 */
public record RegisterDonorResult(
        UUID accountId,
        UUID profileId
) {
}
