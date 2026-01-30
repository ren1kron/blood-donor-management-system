package ifmo.se.coursach_back.report.application.result;

import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import ifmo.se.coursach_back.report.domain.ReportType;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object containing summary information about a report request.
 */
public record ReportRequestSummaryResult(
        UUID requestId,
        UUID donorId,
        String donorName,
        ReportType reportType,
        ReportRequestStatus status,
        String requestedByName,
        String requestedByRole,
        String assignedAdminName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime generatedAt,
        String message
) {
}
