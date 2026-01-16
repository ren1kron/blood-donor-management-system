package ifmo.se.coursach_back.appointment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID slotId
) {
}
