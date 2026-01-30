package ifmo.se.coursach_back.examination.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateExaminationBookingRequest(
        @NotNull(message = "slotId is required")
        UUID slotId
) {}
