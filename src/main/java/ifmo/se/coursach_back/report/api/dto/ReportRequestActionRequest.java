package ifmo.se.coursach_back.report.api.dto;

import jakarta.validation.constraints.Size;

public record ReportRequestActionRequest(
        @Size(max = 2000, message = "Message must not exceed 2000 characters")
        String message
) {
}
