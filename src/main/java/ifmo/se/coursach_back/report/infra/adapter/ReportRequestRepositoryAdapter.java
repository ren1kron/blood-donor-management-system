package ifmo.se.coursach_back.report.infra.adapter;

import ifmo.se.coursach_back.report.application.ports.ReportRequestRepositoryPort;
import ifmo.se.coursach_back.report.domain.ReportRequest;
import ifmo.se.coursach_back.report.domain.ReportRequestStatus;
import ifmo.se.coursach_back.report.infra.jpa.ReportRequestRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReportRequestRepositoryAdapter implements ReportRequestRepositoryPort {
    private final ReportRequestRepository jpaRepository;

    @Override
    public Optional<ReportRequest> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<ReportRequest> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<ReportRequest> findByRequesterId(UUID staffId) {
        return jpaRepository.findByRequesterId(staffId);
    }

    @Override
    public List<ReportRequest> findByStatusOrderByCreatedAtAsc(ReportRequestStatus status) {
        return jpaRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Override
    public List<ReportRequest> findByStatusInOrderByCreatedAtAsc(List<ReportRequestStatus> statuses) {
        return jpaRepository.findByStatusInOrderByCreatedAtAsc(statuses);
    }

    @Override
    public Optional<ReportRequest> findByIdAndRequesterId(UUID requestId, UUID staffId) {
        return jpaRepository.findByIdAndRequesterId(requestId, staffId);
    }

    @Override
    public ReportRequest save(ReportRequest request) {
        return jpaRepository.save(request);
    }
}
