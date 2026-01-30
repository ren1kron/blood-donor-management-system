package ifmo.se.coursach_back.lab.infra.adapter;

import ifmo.se.coursach_back.lab.application.ports.LabExaminationRequestRepositoryPort;
import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import ifmo.se.coursach_back.lab.infra.jpa.LabExaminationRequestRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LabExaminationRequestRepositoryAdapter implements LabExaminationRequestRepositoryPort {
    private final LabExaminationRequestRepository jpaRepository;

    @Override
    public Optional<LabExaminationRequest> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<LabExaminationRequest> findByVisitId(UUID visitId) {
        return jpaRepository.findByVisitId(visitId);
    }

    @Override
    public List<LabExaminationRequest> findByVisitIds(List<UUID> visitIds) {
        return jpaRepository.findByVisitIds(visitIds);
    }

    @Override
    public List<LabExaminationRequest> findByStatuses(List<LabExaminationStatus> statuses) {
        return jpaRepository.findByStatuses(statuses);
    }

    @Override
    public long countByStatusIn(List<LabExaminationStatus> statuses) {
        return jpaRepository.countByStatusIn(statuses);
    }

    @Override
    public LabExaminationRequest save(LabExaminationRequest request) {
        return jpaRepository.save(request);
    }
}
