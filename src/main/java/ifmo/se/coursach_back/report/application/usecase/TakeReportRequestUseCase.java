package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.command.TakeReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;

/**
 * Use case interface for taking/assigning a report request to an admin.
 */
public interface TakeReportRequestUseCase {
    /**
     * Assign a report request to an admin for processing.
     * @param command command containing admin ID and request ID
     * @return summary of the updated report request
     */
    ReportRequestSummaryResult execute(TakeReportRequestCommand command);
}
