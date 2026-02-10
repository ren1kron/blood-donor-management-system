package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.Questionnaire;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, UUID> {
    Optional<Questionnaire> findByVisitId(UUID visitId);
    List<Questionnaire> findByVisitIdIn(List<UUID> visitIds);
    boolean existsByVisitId(UUID visitId);
}
