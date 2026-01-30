package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.QuestionnaireRepositoryPort;
import ifmo.se.coursach_back.medical.domain.Questionnaire;
import ifmo.se.coursach_back.medical.infra.jpa.QuestionnaireRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionnaireRepositoryAdapter implements QuestionnaireRepositoryPort {
    private final QuestionnaireRepository jpaRepository;

    @Override
    public Optional<Questionnaire> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Questionnaire> findByVisitId(UUID visitId) {
        return jpaRepository.findByVisitId(visitId);
    }

    @Override
    public boolean existsByVisitId(UUID visitId) {
        return jpaRepository.existsByVisitId(visitId);
    }

    @Override
    public Questionnaire save(Questionnaire questionnaire) {
        return jpaRepository.save(questionnaire);
    }
}
