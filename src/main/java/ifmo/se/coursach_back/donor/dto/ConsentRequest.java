package ifmo.se.coursach_back.donor.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record ConsentRequest(
        UUID visitId,
        UUID bookingId,
        @NotBlank String consentType
) {
}
