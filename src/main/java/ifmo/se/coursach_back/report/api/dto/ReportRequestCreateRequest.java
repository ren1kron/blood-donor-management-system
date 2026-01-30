package ifmo.se.coursach_back.report.api.dto;

import ifmo.se.coursach_back.report.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ReportRequestCreateRequest(
        @NotNull(message = "Donor ID is required")
        UUID donorId,
        @NotNull(message = "Report type is required")
        ReportType reportType,
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String comment
) {
}
