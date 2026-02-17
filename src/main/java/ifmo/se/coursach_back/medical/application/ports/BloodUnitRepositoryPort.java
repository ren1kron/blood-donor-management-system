package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.BloodUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for BloodUnit repository operations.
 */
public interface BloodUnitRepositoryPort {
    Optional<BloodUnit> findById(UUID id);
    List<BloodUnit> findByStatuses(List<String> statuses);
    List<BloodUnit> findByDonationId(UUID donationId);
    BloodUnit save(BloodUnit bloodUnit);
}
