package ifmo.se.coursach_back.donor.application.usecase;

import java.util.UUID;

/**
 * Use case for acknowledging a notification.
 */
public interface AcknowledgeNotificationUseCase {
    void execute(UUID accountId, UUID deliveryId);
}
