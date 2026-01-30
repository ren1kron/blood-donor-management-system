package ifmo.se.coursach_back.donor.application.ports;

import ifmo.se.coursach_back.donor.domain.Consent;
import java.util.UUID;

/**
 * Port interface for Consent repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface ConsentRepositoryPort {
    Consent save(Consent consent);
}
