package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.Deferral;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Deferral repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface DeferralRepositoryPort {
    Optional<Deferral> findActiveDeferral(UUID donorId, OffsetDateTime now);
    Deferral save(Deferral deferral);
}
