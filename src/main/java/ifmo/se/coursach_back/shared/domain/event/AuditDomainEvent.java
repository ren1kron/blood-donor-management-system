package ifmo.se.coursach_back.shared.domain.event;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain event for audit logging.
 * Published when an auditable action occurs, processed after transaction commit.
 *
 * @param accountId  The account that performed the action (may be null for system actions)
 * @param action     The action type (e.g., ACCOUNT_CREATED, DONATION_REGISTERED)
 * @param entityType The type of entity affected (e.g., Account, Donation)
 * @param entityId   The ID of the affected entity
 * @param metadata   Additional metadata about the action (may be null)
 * @param occurredAt The timestamp when the event occurred
 */
public record AuditDomainEvent(
        UUID accountId,
        String action,
        String entityType,
        UUID entityId,
        Map<String, Object> metadata,
        OffsetDateTime occurredAt
) implements DomainEvent {

    /**
     * Creates an audit event with the current timestamp.
     */
    public static AuditDomainEvent of(UUID accountId, String action, String entityType, UUID entityId, Map<String, Object> metadata) {
        return new AuditDomainEvent(accountId, action, entityType, entityId, metadata, OffsetDateTime.now());
    }

    /**
     * Creates an audit event without metadata.
     */
    public static AuditDomainEvent of(UUID accountId, String action, String entityType, UUID entityId) {
        return of(accountId, action, entityType, entityId, null);
    }
}
