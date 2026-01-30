package ifmo.se.coursach_back.nurse.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CollectionSessionCreateRequest(
        UUID visitId,
        UUID bookingId,
        @Valid VitalsPayload preVitals,
        @Size(max = 2000, message = "Notes must not exceed 2000 characters")
        String notes
) {
}
