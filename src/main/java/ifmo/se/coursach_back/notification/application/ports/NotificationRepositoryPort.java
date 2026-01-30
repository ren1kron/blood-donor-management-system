package ifmo.se.coursach_back.notification.application.ports;

import ifmo.se.coursach_back.notification.domain.Notification;

/**
 * Port interface for Notification repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface NotificationRepositoryPort {
    Notification save(Notification notification);
}
