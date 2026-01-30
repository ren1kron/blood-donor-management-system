package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonorNotificationResponse(
        UUID deliveryId,
        UUID notificationId,
        String topic,
        String body,
        String channel,
        OffsetDateTime createdAt,
        OffsetDateTime sentAt,
        DeliveryStatus status
) {
    public static DonorNotificationResponse from(NotificationDelivery delivery) {
        return new DonorNotificationResponse(
                delivery.getId(),
                delivery.getNotification().getId(),
                delivery.getNotification().getTopic(),
                delivery.getNotification().getBody(),
                delivery.getNotification().getChannel(),
                delivery.getNotification().getCreatedAt(),
                delivery.getSentAt(),
                delivery.getStatus()
        );
    }
}
