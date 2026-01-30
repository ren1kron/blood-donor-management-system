package ifmo.se.coursach_back.appointment.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID slotId
) {
}
