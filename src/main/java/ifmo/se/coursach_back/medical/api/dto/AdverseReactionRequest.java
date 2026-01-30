package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdverseReactionRequest(
        @NotNull(message = "Donation ID is required")
        UUID donationId,
        OffsetDateTime occurredAt,
        String severity,
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description
) {
}
