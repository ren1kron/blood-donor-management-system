package ifmo.se.coursach_back.admin.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for donor summary.
 */
public record DonorSummaryResult(
        UUID donorId,
        String fullName,
        String donorStatus,
        String email,
        String phone,
        OffsetDateTime lastDonationAt,
        OffsetDateTime lastAdmissionAt
) {
}
