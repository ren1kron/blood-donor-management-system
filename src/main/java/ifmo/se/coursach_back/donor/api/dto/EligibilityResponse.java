package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.time.OffsetDateTime;

public record EligibilityResponse(
        DonorStatus donorStatus,
        boolean eligible,
        boolean canBookDonation,
        OffsetDateTime lastDonationAt,
        OffsetDateTime nextEligibleAt,
        OffsetDateTime medicalCheckValidUntil,
        DeferralStatusResponse activeDeferral
) {
}
