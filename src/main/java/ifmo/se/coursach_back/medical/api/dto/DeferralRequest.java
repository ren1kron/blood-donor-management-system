package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record DeferralRequest(
        @NotBlank(message = "Deferral type is required")
        String deferralType,
        @NotBlank(message = "Reason is required")
        @Size(max = 1000, message = "Reason must not exceed 1000 characters")
        String reason,
        OffsetDateTime endsAt
) {
}
