package ifmo.se.coursach_back.notification.infra.adapter;

import ifmo.se.coursach_back.notification.application.ports.NotificationDeliveryRepositoryPort;
import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import ifmo.se.coursach_back.notification.infra.jpa.NotificationDeliveryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationDeliveryRepositoryAdapter implements NotificationDeliveryRepositoryPort {
    private final NotificationDeliveryRepository jpaRepository;

    @Override
    public List<NotificationDelivery> findRecentByDonorId(UUID donorId) {
        return jpaRepository.findRecentByDonorId(donorId);
    }

    @Override
    public Optional<NotificationDelivery> findByIdAndDonorId(UUID deliveryId, UUID donorId) {
        return jpaRepository.findByIdAndDonorId(deliveryId, donorId);
    }

    @Override
    public NotificationDelivery save(NotificationDelivery delivery) {
        return jpaRepository.save(delivery);
    }
}
