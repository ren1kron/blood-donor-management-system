package ifmo.se.coursach_back.report.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReportRequestSummaryResponse(
        UUID id,
        UUID donorId,
        String donorName,
        String reportType,
        String status,
        String requestedByName,
        String requestedByRole,
        String assignedAdminName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OffsetDateTime generatedAt,
        String message
) {
}
