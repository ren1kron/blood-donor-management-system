package ifmo.se.coursach_back.audit.infra.event;

import ifmo.se.coursach_back.audit.application.AuditService;
import ifmo.se.coursach_back.shared.domain.event.AuditDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Event listener for audit domain events.
 * Processes audit events after transaction commit to ensure
 * no phantom audit records are created on rollback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditService auditService;

    /**
     * Handles audit domain events after the transaction has committed.
     * This ensures that audit records are only created for successfully
     * persisted changes.
     *
     * @param event the audit domain event
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAuditEvent(AuditDomainEvent event) {
        log.debug("Processing audit event: action={}, entityType={}, entityId={}",
                event.action(), event.entityType(), event.entityId());

        try {
            auditService.log(
                    event.accountId(),
                    event.action(),
                    event.entityType(),
                    event.entityId(),
                    event.metadata()
            );
            log.debug("Audit event processed successfully: action={}", event.action());
        } catch (Exception e) {
            // Log but don't rethrow - audit failures should not affect the main flow
            log.error("Failed to process audit event: action={}, error={}",
                    event.action(), e.getMessage(), e);
        }
    }
}
