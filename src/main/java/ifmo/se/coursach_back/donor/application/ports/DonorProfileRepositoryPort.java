package ifmo.se.coursach_back.donor.application.ports;

import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for DonorProfile repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface DonorProfileRepositoryPort {
    Optional<DonorProfile> findById(UUID id);
    Optional<DonorProfile> findByAccountId(UUID accountId);
    long count();
    long countByDonorStatus(DonorStatus donorStatus);
    List<DonorProfile> findByDonorStatus(DonorStatus donorStatus);
    List<? extends DonorSummary> findDonorSummaries();
    DonorProfile save(DonorProfile donorProfile);
}
