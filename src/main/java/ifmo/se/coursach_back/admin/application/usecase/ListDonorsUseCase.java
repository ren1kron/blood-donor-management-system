package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.result.DonorSummaryResult;
import java.util.List;

/**
 * Use case interface for listing donors.
 */
public interface ListDonorsUseCase {
    /**
     * List all donors.
     * @return list of donor summary results
     */
    List<DonorSummaryResult> execute();
}
