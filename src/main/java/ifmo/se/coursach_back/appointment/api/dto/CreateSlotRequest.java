package ifmo.se.coursach_back.appointment.api.dto;

import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record CreateSlotRequest(
        @NotNull(message = "Purpose is required")
        SlotPurpose purpose,
        @NotNull(message = "Start time is required")
        OffsetDateTime startAt,
        @NotNull(message = "End time is required")
        OffsetDateTime endAt,
        @NotBlank(message = "Location is required")
        String location,
        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        Integer capacity
) {
}
