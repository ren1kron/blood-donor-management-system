package ifmo.se.coursach_back.donor.dto;

import java.time.OffsetDateTime;

public record EligibilityResponse(
        String donorStatus,
        boolean eligible,
        OffsetDateTime lastDonationAt,
        OffsetDateTime nextEligibleAt,
        DeferralStatusResponse activeDeferral
) {
}
