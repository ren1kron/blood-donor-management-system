package ifmo.se.coursach_back.lab.application.ports;

import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for LabExaminationRequest repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface LabExaminationRequestRepositoryPort {
    Optional<LabExaminationRequest> findById(UUID id);
    Optional<LabExaminationRequest> findByVisitId(UUID visitId);
    List<LabExaminationRequest> findByVisitIds(List<UUID> visitIds);
    List<LabExaminationRequest> findByStatuses(List<LabExaminationStatus> statuses);
    long countByStatusIn(List<LabExaminationStatus> statuses);
    LabExaminationRequest save(LabExaminationRequest request);
}
