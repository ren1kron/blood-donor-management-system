package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.result.EligibleDonorResult;
import java.util.List;

/**
 * Use case interface for listing eligible donors.
 */
public interface ListEligibleDonorsUseCase {
    /**
     * List donors eligible for donation.
     * @param minDaysSinceDonation minimum days since last donation
     * @return list of eligible donor results
     */
    List<EligibleDonorResult> execute(int minDaysSinceDonation);
}
