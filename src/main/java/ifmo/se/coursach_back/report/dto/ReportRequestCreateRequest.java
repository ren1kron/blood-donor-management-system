package ifmo.se.coursach_back.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ReportRequestCreateRequest(
        @NotNull UUID donorId,
        @NotBlank String reportType,
        String comment
) {
}
