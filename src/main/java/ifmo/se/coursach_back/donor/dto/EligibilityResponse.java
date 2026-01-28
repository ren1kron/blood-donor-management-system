package ifmo.se.coursach_back.donor.dto;

import java.time.OffsetDateTime;

public record EligibilityResponse(
        String donorStatus,
        boolean eligible,
        boolean canBookDonation,
        OffsetDateTime lastDonationAt,
        OffsetDateTime nextEligibleAt,
        OffsetDateTime medicalCheckValidUntil,
        DeferralStatusResponse activeDeferral
) {
}
