package ifmo.se.coursach_back.nurse.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record CollectionSessionUpdateRequest(
        @Valid VitalsPayload preVitals,
        @Valid VitalsPayload postVitals,
        @Size(max = 2000, message = "Notes must not exceed 2000 characters")
        String notes,
        @Size(max = 1000, message = "Complications must not exceed 1000 characters")
        String complications,
        @Size(max = 500, message = "Interruption reason must not exceed 500 characters")
        String interruptionReason
) {
}
