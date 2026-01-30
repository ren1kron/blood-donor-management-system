package ifmo.se.coursach_back.admin.api.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationMarkResponse(
        UUID notificationId,
        UUID deliveryId,
        OffsetDateTime sentAt
) {
}
