package ifmo.se.coursach_back.report.application.ports;

import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for ReportRequest repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface ReportRequestRepositoryPort {
    Optional<ReportRequest> findById(UUID id);
    List<ReportRequest> findAll();
    List<ReportRequest> findByRequesterId(UUID staffId);
    List<ReportRequest> findByStatusOrderByCreatedAtAsc(ReportRequestStatus status);
    List<ReportRequest> findByStatusInOrderByCreatedAtAsc(List<ReportRequestStatus> statuses);
    Optional<ReportRequest> findByIdAndRequesterId(UUID requestId, UUID staffId);
    ReportRequest save(ReportRequest request);
}
