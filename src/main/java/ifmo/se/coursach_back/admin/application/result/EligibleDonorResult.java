package ifmo.se.coursach_back.admin.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for eligible donor.
 */
public record EligibleDonorResult(
        UUID donorId,
        String fullName,
        String phone,
        String email,
        OffsetDateTime lastDonationAt,
        long daysSinceDonation
) {
}
