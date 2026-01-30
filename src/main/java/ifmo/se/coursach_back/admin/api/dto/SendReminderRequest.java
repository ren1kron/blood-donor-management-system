package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendReminderRequest(
        @NotNull UUID donorId,
        String channel,  // EMAIL, PHONE
        String topic,
        String body
) {}
