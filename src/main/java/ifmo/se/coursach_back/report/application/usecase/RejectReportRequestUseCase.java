package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.command.RejectReportRequestCommand;
import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;

/**
 * Use case interface for rejecting a report request.
 */
public interface RejectReportRequestUseCase {
    /**
     * Reject a report request with an optional reason.
     * @param command command containing admin ID, request ID, and rejection reason
     * @return summary of the rejected report request
     */
    ReportRequestSummaryResult execute(RejectReportRequestCommand command);
}
