package ifmo.se.coursach_back.appointment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record CreateSlotRequest(
        @NotBlank String purpose,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt,
        @NotBlank String location,
        @NotNull @Min(1) Integer capacity
) {
}
