package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.AuditEvent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}
