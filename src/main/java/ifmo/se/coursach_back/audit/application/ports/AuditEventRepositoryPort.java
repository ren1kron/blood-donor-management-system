package ifmo.se.coursach_back.audit.application.ports;

import ifmo.se.coursach_back.audit.domain.AuditEvent;

/**
 * Port interface for AuditEvent repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface AuditEventRepositoryPort {
    AuditEvent save(AuditEvent auditEvent);
}
