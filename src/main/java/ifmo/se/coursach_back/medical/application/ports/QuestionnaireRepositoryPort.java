package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.Questionnaire;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Questionnaire repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface QuestionnaireRepositoryPort {
    Optional<Questionnaire> findById(UUID id);
    Optional<Questionnaire> findByVisitId(UUID visitId);
    List<Questionnaire> findByVisitIds(List<UUID> visitIds);
    boolean existsByVisitId(UUID visitId);
    Questionnaire save(Questionnaire questionnaire);
}
