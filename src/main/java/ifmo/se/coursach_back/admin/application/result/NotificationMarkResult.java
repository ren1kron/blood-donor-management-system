package ifmo.se.coursach_back.admin.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for notification mark.
 */
public record NotificationMarkResult(
        UUID notificationId,
        UUID deliveryId,
        OffsetDateTime sentAt
) {
}
