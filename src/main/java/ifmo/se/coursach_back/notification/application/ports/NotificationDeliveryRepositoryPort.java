package ifmo.se.coursach_back.notification.application.ports;

import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for NotificationDelivery repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface NotificationDeliveryRepositoryPort {
    List<NotificationDelivery> findRecentByDonorId(UUID donorId);
    Optional<NotificationDelivery> findByIdAndDonorId(UUID deliveryId, UUID donorId);
    NotificationDelivery save(NotificationDelivery delivery);
}
