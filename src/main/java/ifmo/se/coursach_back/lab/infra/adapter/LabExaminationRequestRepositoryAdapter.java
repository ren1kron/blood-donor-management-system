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
    public Optional<LabExaminationRequest> findByVisit_Id(UUID visitId) {
        return jpaRepository.findByVisit_Id(visitId);
    }

    @Override
    public List<LabExaminationRequest> findByVisit_IdIn(List<UUID> visitIds) {
        return jpaRepository.findByVisit_IdIn(visitIds);
    }

    @Override
    public List<LabExaminationRequest> findByStatusInOrderByRequestedAtAsc(List<LabExaminationStatus> statuses) {
        return jpaRepository.findByStatusInOrderByRequestedAtAsc(statuses);
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
