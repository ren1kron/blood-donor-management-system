package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.DonorStatus;
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
