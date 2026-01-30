package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.result.DonationHistoryResult;
import java.util.List;
import java.util.UUID;

/**
 * Use case for listing donor's donation history.
 */
public interface ListDonationHistoryUseCase {
    List<DonationHistoryResult> execute(UUID accountId);
}
