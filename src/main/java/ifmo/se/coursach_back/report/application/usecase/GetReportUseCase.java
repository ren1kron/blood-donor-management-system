package ifmo.se.coursach_back.report.application.usecase;

import ifmo.se.coursach_back.report.application.result.ReportRequestDetailsResult;
import java.util.UUID;

/**
 * Use case interface for retrieving a report with its full details.
 */
public interface GetReportUseCase {
    /**
     * Get the full details of a report request including payload.
     * @param accountId the account ID of the requester
     * @param requestId the ID of the report request
     * @return full report request details
     */
    ReportRequestDetailsResult execute(UUID accountId, UUID requestId);
}
