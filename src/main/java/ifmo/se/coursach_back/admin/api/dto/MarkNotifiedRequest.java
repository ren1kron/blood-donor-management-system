package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.Size;

public record MarkNotifiedRequest(
        String channel,
        @Size(max = 2000, message = "Body must not exceed 2000 characters")
        String body
) {
}
