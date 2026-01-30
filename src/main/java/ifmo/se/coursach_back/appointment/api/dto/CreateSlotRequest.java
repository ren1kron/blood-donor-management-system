package ifmo.se.coursach_back.appointment.api.dto;

import ifmo.se.coursach_back.appointment.domain.SlotPurpose;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record CreateSlotRequest(
        @NotNull SlotPurpose purpose,
        @NotNull OffsetDateTime startAt,
        @NotNull OffsetDateTime endAt,
        @NotNull String location,
        @NotNull @Min(1) Integer capacity
) {
}
