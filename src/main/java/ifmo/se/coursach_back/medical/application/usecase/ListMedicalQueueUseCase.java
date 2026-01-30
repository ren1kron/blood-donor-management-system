package ifmo.se.coursach_back.medical.application.usecase;

import ifmo.se.coursach_back.medical.application.result.ScheduledDonorResult;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Use case for listing the medical queue (scheduled donors).
 */
public interface ListMedicalQueueUseCase {
    List<ScheduledDonorResult> execute(OffsetDateTime from);
}
