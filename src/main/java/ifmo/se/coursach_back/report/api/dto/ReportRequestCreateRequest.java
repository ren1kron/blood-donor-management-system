package ifmo.se.coursach_back.report.api.dto;

import ifmo.se.coursach_back.report.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReportRequestCreateRequest(
        @NotNull UUID donorId,
        @NotNull ReportType reportType,
        String comment
) {
}
