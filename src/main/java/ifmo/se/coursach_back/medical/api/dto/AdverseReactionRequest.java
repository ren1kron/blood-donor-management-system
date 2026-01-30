package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AdverseReactionRequest(
        @NotNull UUID donationId,
        OffsetDateTime occurredAt,
        String severity,
        String description
) {
}
