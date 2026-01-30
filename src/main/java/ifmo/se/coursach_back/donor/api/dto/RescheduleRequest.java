package ifmo.se.coursach_back.donor.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RescheduleRequest(
        @NotNull UUID newSlotId
) {
}
