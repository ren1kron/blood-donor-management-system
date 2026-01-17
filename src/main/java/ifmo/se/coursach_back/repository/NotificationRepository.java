package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Notification;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
