package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.admin.api.dto.EligibleDonorRow;
import ifmo.se.coursach_back.medical.domain.Donation;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Donation repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface DonationRepositoryPort {
    Optional<Donation> findById(UUID id);
    Optional<Donation> findByVisitId(UUID visitId);
    List<EligibleDonorRow> findEligibleDonors(OffsetDateTime threshold);
    List<Donation> findByDonorAccountId(UUID accountId);
    List<Donation> findPublishedByDonorAccountId(UUID accountId);
    Optional<Donation> findLatestByDonorAccountId(UUID accountId);
    List<Donation> findByVisitIds(List<UUID> visitIds);
    long countByPerformedAtBetween(OffsetDateTime from, OffsetDateTime to);
    List<Object[]> sumVolumeByBloodTypeAndRh(OffsetDateTime from, OffsetDateTime to);
    Donation save(Donation donation);
}
