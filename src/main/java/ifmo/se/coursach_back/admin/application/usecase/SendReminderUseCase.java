package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.application.command.SendReminderCommand;
import ifmo.se.coursach_back.admin.application.result.ReminderSentResult;

/**
 * Use case interface for sending a reminder to a donor.
 */
public interface SendReminderUseCase {
    /**
     * Send a reminder to a donor.
     * @param command the command containing reminder details
     * @return the reminder sent result
     */
    ReminderSentResult execute(SendReminderCommand command);
}
