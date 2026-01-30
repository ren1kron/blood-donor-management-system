package ifmo.se.coursach_back.notification.infra.adapter;

import ifmo.se.coursach_back.notification.application.ports.NotificationRepositoryPort;
import ifmo.se.coursach_back.notification.domain.Notification;
import ifmo.se.coursach_back.notification.infra.jpa.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryAdapter implements NotificationRepositoryPort {
    private final NotificationRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        return jpaRepository.save(notification);
    }
}
