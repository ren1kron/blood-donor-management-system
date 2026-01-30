package ifmo.se.coursach_back.report.infra.jpa;

import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, UUID> {
    List<ReportRequest> findByRequestedBy_IdOrderByCreatedAtDesc(UUID staffId);

    List<ReportRequest> findByStatusOrderByCreatedAtAsc(ReportRequestStatus status);

    List<ReportRequest> findByStatusInOrderByCreatedAtAsc(List<ReportRequestStatus> statuses);

    Optional<ReportRequest> findByIdAndRequestedBy_Id(UUID requestId, UUID staffId);
}
