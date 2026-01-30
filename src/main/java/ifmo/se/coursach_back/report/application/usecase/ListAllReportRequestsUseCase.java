package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import java.util.List;

/**
 * Use case interface for listing all report requests (admin view).
 */
public interface ListAllReportRequestsUseCase {
    /**
     * List all report requests in the system, optionally filtered by status.
     * @param status optional status filter (e.g., "REQUESTED", "IN_PROGRESS")
     * @return list of report request summaries
     */
    List<ReportRequestSummaryResult> execute(String status);
}
