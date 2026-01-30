package ifmo.se.coursach_back.shared.domain.event;

import ifmo.se.coursach_back.donor.domain.DonorProfile;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Domain event for sending notifications.
 * Published when a notification needs to be sent, processed after transaction commit.
 *
 * @param donor          The donor to send notification to
 * @param topic          The notification topic/type
 * @param body           The notification body text
 * @param channel        The notification channel (email, sms, push, IN_APP)
 * @param staffAccountId The staff account that triggered the notification (may be null)
 * @param occurredAt     The timestamp when the event occurred
 */
public record NotificationDomainEvent(
        DonorProfile donor,
        String topic,
        String body,
        String channel,
        UUID staffAccountId,
        OffsetDateTime occurredAt
) implements DomainEvent {

    /**
     * Creates a notification event with the current timestamp.
     */
    public static NotificationDomainEvent of(DonorProfile donor, String topic, String body, String channel, UUID staffAccountId) {
        return new NotificationDomainEvent(donor, topic, body, channel, staffAccountId, OffsetDateTime.now());
    }

    /**
     * Creates a notification event with IN_APP channel.
     */
    public static NotificationDomainEvent inApp(DonorProfile donor, String topic, String body) {
        return of(donor, topic, body, "IN_APP", null);
    }

    /**
     * Creates a notification event with IN_APP channel and staff reference.
     */
    public static NotificationDomainEvent inApp(DonorProfile donor, String topic, String body, UUID staffAccountId) {
        return of(donor, topic, body, "IN_APP", staffAccountId);
    }
}
