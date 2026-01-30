package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.result.NotificationResult;
import java.util.List;
import java.util.UUID;

/**
 * Use case for listing donor notifications.
 */
public interface ListDonorNotificationsUseCase {
    List<NotificationResult> execute(UUID accountId);
}
