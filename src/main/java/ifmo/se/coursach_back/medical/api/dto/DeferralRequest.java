package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;

public record DeferralRequest(
        @NotBlank String deferralType,
        @NotBlank String reason,
        OffsetDateTime endsAt
) {
}
