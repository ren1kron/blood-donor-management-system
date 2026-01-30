package ifmo.se.coursach_back.notification.infra.jpa;

import ifmo.se.coursach_back.notification.domain.NotificationDelivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
    @Query("""
            select d
            from NotificationDelivery d
            where d.donor.id = :donorId
            order by d.sentAt desc
            """)
    List<NotificationDelivery> findRecentByDonorId(@Param("donorId") UUID donorId);

    Optional<NotificationDelivery> findByIdAndDonorId(UUID deliveryId, UUID donorId);
}
