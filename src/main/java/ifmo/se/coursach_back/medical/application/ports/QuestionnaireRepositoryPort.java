package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.Questionnaire;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for Questionnaire repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface QuestionnaireRepositoryPort {
    Optional<Questionnaire> findById(UUID id);
    Optional<Questionnaire> findByVisit_Id(UUID visitId);
    boolean existsByVisit_Id(UUID visitId);
    Questionnaire save(Questionnaire questionnaire);
}
