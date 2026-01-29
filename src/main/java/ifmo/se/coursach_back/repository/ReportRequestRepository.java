package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.ReportRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, UUID> {
    List<ReportRequest> findByRequestedBy_IdOrderByCreatedAtDesc(UUID staffId);

    List<ReportRequest> findByStatusOrderByCreatedAtAsc(String status);

    List<ReportRequest> findByStatusInOrderByCreatedAtAsc(List<String> statuses);

    Optional<ReportRequest> findByIdAndRequestedBy_Id(UUID requestId, UUID staffId);
}
