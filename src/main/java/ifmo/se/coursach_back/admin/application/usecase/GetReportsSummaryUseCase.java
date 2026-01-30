package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.result.ReportsSummaryResult;
import java.time.OffsetDateTime;

/**
 * Use case interface for getting reports summary.
 */
public interface GetReportsSummaryUseCase {
    /**
     * Get reports summary.
     * @param from start date for the report period
     * @param to end date for the report period
     * @return the reports summary result
     */
    ReportsSummaryResult execute(OffsetDateTime from, OffsetDateTime to);
}
