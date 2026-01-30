package ifmo.se.coursach_back.notification.infra.jpa;

import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
    List<NotificationDelivery> findByDonor_IdOrderBySentAtDesc(UUID donorId);

    Optional<NotificationDelivery> findByIdAndDonor_Id(UUID deliveryId, UUID donorId);
}
