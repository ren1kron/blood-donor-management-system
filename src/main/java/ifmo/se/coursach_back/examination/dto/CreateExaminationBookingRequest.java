package ifmo.se.coursach_back.examination.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateExaminationBookingRequest(
        @NotNull(message = "slotId is required")
        UUID slotId
) {}
