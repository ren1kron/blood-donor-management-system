package ifmo.se.coursach_back.donor.application.result;

import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result for notification listing.
 */
public record NotificationResult(
        UUID deliveryId,
        UUID notificationId,
        String topic,
        String body,
        String channel,
        OffsetDateTime createdAt,
        OffsetDateTime sentAt,
        DeliveryStatus status
) {
}
