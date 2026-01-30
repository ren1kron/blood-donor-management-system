package ifmo.se.coursach_back.report.dto;

import ifmo.se.coursach_back.model.ReportType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReportRequestCreateRequest(
        @NotNull UUID donorId,
        @NotNull ReportType reportType,
        String comment
) {
}
