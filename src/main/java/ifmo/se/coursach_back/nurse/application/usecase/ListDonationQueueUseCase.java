package ifmo.se.coursach_back.nurse.application.usecase;

import ifmo.se.coursach_back.nurse.application.result.DonationQueueResult;
import java.time.OffsetDateTime;

/**
 * Use case interface for listing the donation queue.
 */
public interface ListDonationQueueUseCase {
    /**
     * List scheduled donors in the donation queue.
     * @param from optional start time filter; if null, defaults to 2 hours ago
     * @return result containing list of donors with booking, check, donation, and session info
     */
    DonationQueueResult execute(OffsetDateTime from);
}
