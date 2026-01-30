package ifmo.se.coursach_back.donor.application.usecase;

import ifmo.se.coursach_back.donor.application.DonorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcknowledgeNotificationService implements AcknowledgeNotificationUseCase {
    private final DonorService donorService;

    @Override
    public void execute(UUID accountId, UUID deliveryId) {
        donorService.acknowledgeNotification(accountId, deliveryId);
    }
}
