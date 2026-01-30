package ifmo.se.coursach_back.audit.infra.jpa;

import ifmo.se.coursach_back.audit.domain.AuditEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}
