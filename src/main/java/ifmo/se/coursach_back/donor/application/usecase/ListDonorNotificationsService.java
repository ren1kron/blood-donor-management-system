package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.DonorService;
import ifmo.se.coursach_back.donor.application.result.NotificationResult;
import ifmo.se.coursach_back.notification.domain.DeliveryStatus;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListDonorNotificationsService implements ListDonorNotificationsUseCase {
    private final DonorService donorService;

    @Override
    public List<NotificationResult> execute(UUID accountId) {
        List<NotificationDelivery> deliveries = donorService.listNotifications(accountId);
        return deliveries.stream()
                .map(d -> new NotificationResult(
                        d.getId(),
                        d.getNotification() != null ? d.getNotification().getId() : null,
                        d.getNotification() != null ? d.getNotification().getTopic() : null,
                        d.getNotification() != null ? d.getNotification().getBody() : null,
                        d.getNotification() != null ? d.getNotification().getChannel() : null,
                        d.getNotification() != null ? d.getNotification().getCreatedAt() : null,
                        d.getSentAt(),
                        d.getStatus()
                ))
                .toList();
    }
}
