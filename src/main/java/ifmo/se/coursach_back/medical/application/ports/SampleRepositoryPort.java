package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.Sample;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Sample repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface SampleRepositoryPort {
    Optional<Sample> findById(UUID id);
    boolean existsBySampleCode(String sampleCode);
    List<Sample> findByStatuses(List<String> statuses);
    long countByCollectedAtBetween(OffsetDateTime from, OffsetDateTime to);
    List<Sample> findByDonorId(UUID donorId);
    Sample save(Sample sample);
}
