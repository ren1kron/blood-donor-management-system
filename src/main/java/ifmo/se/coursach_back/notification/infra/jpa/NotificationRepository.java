package ifmo.se.coursach_back.notification.infra.jpa;

import ifmo.se.coursach_back.notification.domain.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
