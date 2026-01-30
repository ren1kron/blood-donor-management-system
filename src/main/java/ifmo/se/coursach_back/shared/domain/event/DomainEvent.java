package ifmo.se.coursach_back.shared.domain.event;

import java.time.OffsetDateTime;

/**
 * Base interface for all domain events.
 * All domain events must implement this interface to be published via DomainEventPublisher.
 */
public sealed interface DomainEvent permits AuditDomainEvent, NotificationDomainEvent {
    
    /**
     * The timestamp when the event occurred.
     */
    OffsetDateTime occurredAt();
}
