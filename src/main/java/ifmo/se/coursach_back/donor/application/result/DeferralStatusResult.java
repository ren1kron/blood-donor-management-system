package ifmo.se.coursach_back.donor.application.result;

import ifmo.se.coursach_back.medical.domain.DeferralType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for deferral status.
 */
public record DeferralStatusResult(
        UUID deferralId,
        DeferralType deferralType,
        String reason,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt
) {
}
