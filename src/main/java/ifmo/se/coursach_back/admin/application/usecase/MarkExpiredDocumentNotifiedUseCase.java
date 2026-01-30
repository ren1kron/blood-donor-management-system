package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.MarkNotifiedCommand;
import ifmo.se.coursach_back.admin.application.result.NotificationMarkResult;

/**
 * Use case interface for marking an expired document as notified.
 */
public interface MarkExpiredDocumentNotifiedUseCase {
    /**
     * Mark an expired document as notified.
     * @param command the command containing notification details
     * @return the notification mark result
     */
    NotificationMarkResult execute(MarkNotifiedCommand command);
}
