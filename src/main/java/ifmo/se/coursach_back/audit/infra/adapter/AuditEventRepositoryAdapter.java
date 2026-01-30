package ifmo.se.coursach_back.audit.infra.adapter;

import ifmo.se.coursach_back.audit.application.ports.AuditEventRepositoryPort;
import ifmo.se.coursach_back.audit.domain.AuditEvent;
import ifmo.se.coursach_back.audit.infra.jpa.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditEventRepositoryAdapter implements AuditEventRepositoryPort {
    private final AuditEventRepository jpaRepository;

    @Override
    public AuditEvent save(AuditEvent auditEvent) {
        return jpaRepository.save(auditEvent);
    }
}
