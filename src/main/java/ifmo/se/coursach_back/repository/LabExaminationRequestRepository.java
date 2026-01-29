package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.LabExaminationRequest;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabExaminationRequestRepository extends JpaRepository<LabExaminationRequest, UUID> {
    Optional<LabExaminationRequest> findByVisit_Id(UUID visitId);

    List<LabExaminationRequest> findByVisit_IdIn(List<UUID> visitIds);

    List<LabExaminationRequest> findByStatusInOrderByRequestedAtAsc(List<String> statuses);
}
