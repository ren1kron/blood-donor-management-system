package ifmo.se.coursach_back.examination.application.usecase;

import ifmo.se.coursach_back.examination.application.result.ExaminationSlotResult;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Use case interface for listing available examination slots.
 */
public interface ListExaminationSlotsUseCase {
    /**
     * List available examination slots within a date range.
     * @param from start of the date range (optional, defaults to now)
     * @param to end of the date range (optional, defaults to 30 days from now)
     * @return list of available examination slots
     */
    List<ExaminationSlotResult> execute(OffsetDateTime from, OffsetDateTime to);
}
