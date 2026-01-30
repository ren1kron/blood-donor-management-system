package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record SendReminderRequest(
        @NotNull(message = "Donor ID is required")
        UUID donorId,
        String channel,  // EMAIL, PHONE
        String topic,
        @Size(max = 2000, message = "Body must not exceed 2000 characters")
        String body
) {}
