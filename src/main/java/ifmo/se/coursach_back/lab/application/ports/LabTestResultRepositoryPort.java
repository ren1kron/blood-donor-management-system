package ifmo.se.coursach_back.lab.application.ports;

import ifmo.se.coursach_back.lab.domain.LabTestResult;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for LabTestResult repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface LabTestResultRepositoryPort {
    Optional<LabTestResult> findById(UUID id);
    Optional<LabTestResult> findBySample_IdAndTestType_Id(UUID sampleId, Short testTypeId);
    List<LabTestResult> findBySample_Id(UUID sampleId);
    List<LabTestResult> findPublishedByDonorAccountId(UUID accountId);
    long countPublishedByTestedAtBetween(OffsetDateTime from, OffsetDateTime to);
    List<LabTestResult> findByDonorId(UUID donorId);
    LabTestResult save(LabTestResult result);
}
