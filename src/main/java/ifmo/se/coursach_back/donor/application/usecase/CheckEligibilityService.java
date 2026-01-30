package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.api.dto.EligibilityResponse;
import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.result.DeferralStatusResult;
import ifmo.se.coursach_back.donor.application.result.EligibilityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckEligibilityService implements CheckEligibilityUseCase {
    private final DonorService donorService;

    @Override
    public EligibilityResult execute(UUID accountId) {
        EligibilityResponse response = donorService.getEligibility(accountId);
        DeferralStatusResult deferral = null;
        if (response.activeDeferral() != null) {
            var d = response.activeDeferral();
            deferral = new DeferralStatusResult(
                    d.deferralId(),
                    d.deferralType(),
                    d.reason(),
                    d.startsAt(),
                    d.endsAt()
            );
        }
        return new EligibilityResult(
                response.donorStatus(),
                response.eligible(),
                response.canBookDonation(),
                response.lastDonationAt(),
                response.nextEligibleAt(),
                response.medicalCheckValidUntil(),
                deferral
        );
    }
}
