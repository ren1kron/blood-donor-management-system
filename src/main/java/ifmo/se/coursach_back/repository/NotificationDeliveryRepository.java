package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.NotificationDelivery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
    List<NotificationDelivery> findByDonor_IdOrderBySentAtDesc(UUID donorId);

    Optional<NotificationDelivery> findByIdAndDonor_Id(UUID deliveryId, UUID donorId);

    @Modifying
    @Query(value = "call sp_ack_notification(:deliveryId, :donorId)", nativeQuery = true)
    void acknowledgeDelivery(@Param("deliveryId") UUID deliveryId, @Param("donorId") UUID donorId);
}
