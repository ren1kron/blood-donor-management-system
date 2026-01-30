package ifmo.se.coursach_back.admin.application.command;

import java.util.UUID;

/**
 * Command object for marking a donor or document as notified.
 */
public record MarkNotifiedCommand(
        UUID adminAccountId,
        UUID targetId,
        String channel,
        String message
) {
}
