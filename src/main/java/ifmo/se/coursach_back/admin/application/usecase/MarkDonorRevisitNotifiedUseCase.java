package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.MarkNotifiedCommand;
import ifmo.se.coursach_back.admin.application.result.NotificationMarkResult;

/**
 * Use case interface for marking a donor as notified for revisit.
 */
public interface MarkDonorRevisitNotifiedUseCase {
    /**
     * Mark a donor as notified for revisit.
     * @param command the command containing notification details
     * @return the notification mark result
     */
    NotificationMarkResult execute(MarkNotifiedCommand command);
}
