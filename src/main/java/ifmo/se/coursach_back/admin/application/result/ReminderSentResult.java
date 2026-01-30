package ifmo.se.coursach_back.admin.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for reminder sent.
 */
public record ReminderSentResult(
        UUID notificationId,
        UUID deliveryId,
        String status,
        OffsetDateTime sentAt
) {
}
