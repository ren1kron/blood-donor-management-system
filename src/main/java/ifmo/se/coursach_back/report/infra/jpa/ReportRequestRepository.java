package ifmo.se.coursach_back.report.infra.jpa;

import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRequestRepository extends JpaRepository<ReportRequest, UUID> {
    @Query("""
            select r
            from ReportRequest r
            where r.requestedBy.id = :staffId
            order by r.createdAt desc
            """)
    List<ReportRequest> findByRequesterId(@Param("staffId") UUID staffId);

    List<ReportRequest> findByStatusOrderByCreatedAtAsc(ReportRequestStatus status);

    List<ReportRequest> findByStatusInOrderByCreatedAtAsc(List<ReportRequestStatus> statuses);

    @Query("""
            select r
            from ReportRequest r
            where r.id = :requestId
              and r.requestedBy.id = :staffId
            """)
    Optional<ReportRequest> findByIdAndRequesterId(
            @Param("requestId") UUID requestId,
            @Param("staffId") UUID staffId);
}
