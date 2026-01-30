package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.SendReminderRequest;
import ifmo.se.coursach_back.admin.api.dto.SendReminderResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.command.SendReminderCommand;
import ifmo.se.coursach_back.admin.application.result.ReminderSentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of SendReminderUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class SendReminderService implements SendReminderUseCase {
    private final AdminService adminService;

    @Override
    public ReminderSentResult execute(SendReminderCommand command) {
        SendReminderRequest request = new SendReminderRequest(
                command.donorId(),
                command.topic(),
                command.message(),
                command.channel()
        );
        SendReminderResponse response = adminService.sendReminder(
                command.adminAccountId(),
                request
        );
        return new ReminderSentResult(
                response.notificationId(),
                response.deliveryId(),
                response.status(),
                response.sentAt()
        );
    }
}
