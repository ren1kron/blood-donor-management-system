package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.result.ReportRequestSummaryResult;
import java.util.List;
import java.util.UUID;

/**
 * Use case interface for listing report requests created by the current user.
 */
public interface ListMyReportRequestsUseCase {
    /**
     * List all report requests created by the specified account.
     * @param accountId the account ID of the requester
     * @return list of report request summaries
     */
    List<ReportRequestSummaryResult> execute(UUID accountId);
}
