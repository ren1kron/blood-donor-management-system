package ifmo.se.coursach_back.admin.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationMarkResponse(
        UUID notificationId,
        UUID deliveryId,
        OffsetDateTime sentAt
) {
}
