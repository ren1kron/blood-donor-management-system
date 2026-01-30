package ifmo.se.coursach_back.admin.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SendReminderResponse(
        UUID notificationId,
        UUID deliveryId,
        String status,
        OffsetDateTime sentAt
) {}
