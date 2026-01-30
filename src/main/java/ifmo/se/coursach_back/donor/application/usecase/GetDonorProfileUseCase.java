package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.result.DonorProfileResult;
import java.util.UUID;

/**
 * Use case for retrieving donor profile.
 */
public interface GetDonorProfileUseCase {
    DonorProfileResult execute(UUID accountId);
}
