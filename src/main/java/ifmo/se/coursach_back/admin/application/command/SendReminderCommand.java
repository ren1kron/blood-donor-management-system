package ifmo.se.coursach_back.admin.application.command;

import java.util.UUID;

/**
 * Command object for sending a reminder to a donor.
 */
public record SendReminderCommand(
        UUID adminAccountId,
        UUID donorId,
        String topic,
        String message,
        String channel
) {
}
