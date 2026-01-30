package ifmo.se.coursach_back.admin.application.usecase;

import ifmo.se.coursach_back.admin.api.dto.MarkNotifiedRequest;
import ifmo.se.coursach_back.admin.api.dto.NotificationMarkResponse;
import ifmo.se.coursach_back.admin.application.AdminService;
import ifmo.se.coursach_back.admin.application.command.MarkNotifiedCommand;
import ifmo.se.coursach_back.admin.application.result.NotificationMarkResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of MarkExpiredDocumentNotifiedUseCase that delegates to AdminService.
 */
@Service
@RequiredArgsConstructor
public class MarkExpiredDocumentNotifiedService implements MarkExpiredDocumentNotifiedUseCase {
    private final AdminService adminService;

    @Override
    public NotificationMarkResult execute(MarkNotifiedCommand command) {
        MarkNotifiedRequest request = new MarkNotifiedRequest(
                command.channel(),
                command.message()
        );
        NotificationMarkResponse response = adminService.markExpiredDocumentNotified(
                command.adminAccountId(),
                command.targetId(),
                request
        );
        return new NotificationMarkResult(
                response.notificationId(),
                response.deliveryId(),
                response.sentAt()
        );
    }
}
