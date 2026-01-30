package ifmo.se.coursach_back.admin.application.ports;

import ifmo.se.coursach_back.admin.domain.StaffProfile;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for StaffProfile repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface StaffProfileRepositoryPort {
    Optional<StaffProfile> findById(UUID id);
    Optional<StaffProfile> findByAccountId(UUID accountId);
    List<StaffProfile> findAll();
    StaffProfile save(StaffProfile staffProfile);
}
