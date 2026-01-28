package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Questionnaire;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionnaireRepository extends JpaRepository<Questionnaire, UUID> {
    Optional<Questionnaire> findByVisit_Id(UUID visitId);
    
    boolean existsByVisit_Id(UUID visitId);
}
