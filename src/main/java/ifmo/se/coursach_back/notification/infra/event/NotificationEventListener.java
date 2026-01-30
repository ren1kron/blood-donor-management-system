package ifmo.se.coursach_back.notification.infra.event;

import ifmo.se.coursach_back.notification.application.NotificationService;
import ifmo.se.coursach_back.notification.application.NotificationService.NotificationRequest;
import ifmo.se.coursach_back.shared.domain.event.NotificationDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for notification domain events.
 * Processes notification events after transaction commit to ensure
 * notifications are only sent for successfully persisted changes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * Handles notification domain events after the transaction has committed.
     * This ensures that notifications are only sent for successfully
     * persisted changes, avoiding phantom notifications on rollback.
     *
     * @param event the notification domain event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationDomainEvent event) {
        log.debug("Processing notification event: topic={}, donorId={}",
                event.topic(), event.donor() != null ? event.donor().getId() : null);

        try {
            NotificationRequest request = NotificationRequest.builder()
                    .channel(event.channel())
                    .topic(event.topic())
                    .body(event.body())
                    .donor(event.donor())
                    .staffAccountId(event.staffAccountId())
                    .build();

            notificationService.sendToDonor(request);

            log.debug("Notification event processed successfully: topic={}", event.topic());
        } catch (Exception e) {
            // Log but don't rethrow - notification failures should not affect the main flow
            log.error("Failed to process notification event: topic={}, error={}",
                    event.topic(), e.getMessage(), e);
        }
    }
}
