package ifmo.se.coursach_back.medical.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonationRequest(
        UUID bookingId,
        UUID visitId,
        @NotBlank(message = "Donation type is required")
        String donationType,
        @Min(value = 1, message = "Volume must be at least 1 ml")
        @Max(value = 1000, message = "Volume must not exceed 1000 ml")
        Integer volumeMl,
        OffsetDateTime performedAt
) {
}
