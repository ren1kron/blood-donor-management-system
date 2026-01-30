package ifmo.se.coursach_back.shared.application.ports;

import ifmo.se.coursach_back.shared.domain.Role;
import java.util.Optional;

/**
 * Port interface for Role repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface RoleRepositoryPort {
    Optional<Role> findById(Short id);
    Optional<Role> findByCode(String code);
}
