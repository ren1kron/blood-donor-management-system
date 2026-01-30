package ifmo.se.coursach_back.donor.application.result;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.time.OffsetDateTime;

/**
 * Result for eligibility check.
 */
public record EligibilityResult(
        DonorStatus donorStatus,
        boolean eligible,
        boolean canBookDonation,
        OffsetDateTime lastDonationAt,
        OffsetDateTime nextEligibleAt,
        OffsetDateTime medicalCheckValidUntil,
        DeferralStatusResult activeDeferral
) {
}
