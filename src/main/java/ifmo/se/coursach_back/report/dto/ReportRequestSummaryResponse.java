package ifmo.se.coursach_back.report.dto;

import ifmo.se.coursach_back.model.ReportType;
import ifmo.se.coursach_back.model.ReportRequestStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportRequestSummaryResponse(
        UUID id,
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
