package ifmo.se.coursach_back.shared.application.ports;

import ifmo.se.coursach_back.shared.domain.event.DomainEvent;

/**
 * Port for publishing domain events.
 * Domain events are processed asynchronously after transaction commit
 * to ensure no phantom records are created on rollback.
 */
public interface DomainEventPublisher {

    /**
     * Publishes a domain event to be processed after the current transaction commits.
     * If there is no active transaction, the event is processed immediately.
     *
     * @param event the domain event to publish
     */
    void publish(DomainEvent event);
}
