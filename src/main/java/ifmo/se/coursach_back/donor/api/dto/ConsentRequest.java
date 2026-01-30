package ifmo.se.coursach_back.donor.api.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ConsentRequest(
        UUID visitId,
        UUID bookingId,
        @NotBlank(message = "Consent type is required")
        String consentType
) {
}
