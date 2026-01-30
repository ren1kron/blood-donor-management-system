package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.command.CreateReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;

/**
 * Use case interface for creating a report request.
 */
public interface CreateReportRequestUseCase {
    /**
     * Create a new report request for a donor.
     * @param command command containing report request details
     * @return summary of the created report request
     */
    ReportRequestSummaryResult execute(CreateReportRequestCommand command);
}
