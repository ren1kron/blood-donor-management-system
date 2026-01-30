package ifmo.se.coursach_back.notification.application;

import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.notification.domain.Notification;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.admin.domain.StaffProfile;
import ifmo.se.coursach_back.notification.infra.jpa.NotificationDeliveryRepository;
import ifmo.se.coursach_back.notification.infra.jpa.NotificationRepository;
import ifmo.se.coursach_back.admin.infra.jpa.StaffProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static ifmo.se.coursach_back.shared.util.StringUtils.normalize;

/**
 * Centralized service for all notification operations.
 * Provides a single point for creating, sending, and managing notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String DEFAULT_CHANNEL = "email";

    private final NotificationRepository notificationRepository;
    private final NotificationDeliveryRepository notificationDeliveryRepository;
    private final StaffProfileRepository staffProfileRepository;

    /**
     * Creates and sends a notification to a donor.
     *
     * @param request notification creation request
     * @return the created notification result
     */
    @Transactional
    public NotificationResult sendToDonor(NotificationRequest request) {
        validateRequest(request);

        Notification notification = createNotification(request);
        Notification savedNotification = notificationRepository.save(notification);

        NotificationDelivery delivery = createDelivery(savedNotification, request);
        NotificationDelivery savedDelivery = notificationDeliveryRepository.save(delivery);

        log.info("Notification sent: topic={}, donorId={}, channel={}",
                request.topic(), getDonorId(request), request.channel());

        return new NotificationResult(
                savedNotification.getId(),
                savedDelivery.getId(),
                delivery.getSentAt()
        );
    }

    /**
     * Creates a notification without sending (for batch operations).
     *
     * @param channel notification channel (email, sms, push)
     * @param topic   notification topic/type
     * @param body    notification body text
     * @return created notification
     */
    @Transactional
    public Notification createOnly(String channel, String topic, String body) {
        Notification notification = new Notification();
        notification.setChannel(resolveChannel(channel));
        notification.setTopic(topic);
        notification.setBody(body);
        return notificationRepository.save(notification);
    }

    /**
     * Creates a delivery record for a notification.
     *
     * @param notification the notification
     * @param donor        the recipient donor
     * @param staffId      optional staff member who triggered the notification
     * @param status       delivery status
     * @return created delivery record
     */
    @Transactional
    public NotificationDelivery createDeliveryRecord(
            Notification notification,
            DonorProfile donor,
            UUID staffId,
            DeliveryStatus status
    ) {
        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(notification);
        delivery.setDonor(donor);
        delivery.setStatus(status);

        if (staffId != null) {
            staffProfileRepository.findByAccountId(staffId).ifPresent(delivery::setStaff);
        }

        if (status == DeliveryStatus.SENT) {
            delivery.setSentAt(OffsetDateTime.now());
        }

        return notificationDeliveryRepository.save(delivery);
    }

    private void validateRequest(NotificationRequest request) {
        if (request.topic() == null || request.topic().isBlank()) {
            throw new IllegalArgumentException("Notification topic is required");
        }
        if (request.body() == null || request.body().isBlank()) {
            throw new IllegalArgumentException("Notification body is required");
        }
        if (request.donor() == null) {
            throw new IllegalArgumentException("Recipient donor is required");
        }
    }

    private Notification createNotification(NotificationRequest request) {
        Notification notification = new Notification();
        notification.setChannel(resolveChannel(request.channel()));
        notification.setTopic(request.topic());
        notification.setBody(request.body());
        return notification;
    }

    private NotificationDelivery createDelivery(Notification notification, NotificationRequest request) {
        NotificationDelivery delivery = new NotificationDelivery();
        delivery.setNotification(notification);
        delivery.setDonor(request.donor());
        delivery.setStatus(DeliveryStatus.SENT);
        delivery.setSentAt(OffsetDateTime.now());

        if (request.staffAccountId() != null) {
            staffProfileRepository.findByAccountId(request.staffAccountId())
                    .ifPresent(delivery::setStaff);
        }

        return delivery;
    }

    private String resolveChannel(String channel) {
        String normalized = normalize(channel);
        return normalized != null ? normalized : DEFAULT_CHANNEL;
    }

    private UUID getDonorId(NotificationRequest request) {
        return Optional.ofNullable(request.donor())
                .map(DonorProfile::getId)
                .orElse(null);
    }

    /**
     * Request object for creating notifications.
     */
    public record NotificationRequest(
            String channel,
            String topic,
            String body,
            DonorProfile donor,
            UUID staffAccountId
    ) {
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String channel;
            private String topic;
            private String body;
            private DonorProfile donor;
            private UUID staffAccountId;

            public Builder channel(String channel) {
                this.channel = channel;
                return this;
            }

            public Builder topic(String topic) {
                this.topic = topic;
                return this;
            }

            public Builder body(String body) {
                this.body = body;
                return this;
            }

            public Builder donor(DonorProfile donor) {
                this.donor = donor;
                return this;
            }

            public Builder staffAccountId(UUID staffAccountId) {
                this.staffAccountId = staffAccountId;
                return this;
            }

            public NotificationRequest build() {
                return new NotificationRequest(channel, topic, body, donor, staffAccountId);
            }
        }
    }

    /**
     * Result of a notification operation.
     */
    public record NotificationResult(
            UUID notificationId,
            UUID deliveryId,
            OffsetDateTime sentAt
    ) {
    }
}
