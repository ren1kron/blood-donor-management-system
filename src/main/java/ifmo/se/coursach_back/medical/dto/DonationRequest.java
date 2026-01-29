package ifmo.se.coursach_back.medical.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonationRequest(
        UUID bookingId,
        UUID visitId,
        @NotBlank String donationType,
        Integer volumeMl,
        OffsetDateTime performedAt
) {
}
