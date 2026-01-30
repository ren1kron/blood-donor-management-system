package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.result.EligibilityResult;
import java.util.UUID;

/**
 * Use case for checking donor eligibility.
 */
public interface CheckEligibilityUseCase {
    EligibilityResult execute(UUID accountId);
}
