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
    public Optional<Questionnaire> findByVisit_Id(UUID visitId) {
        return jpaRepository.findByVisit_Id(visitId);
    }

    @Override
    public boolean existsByVisit_Id(UUID visitId) {
        return jpaRepository.existsByVisit_Id(visitId);
    }

    @Override
    public Questionnaire save(Questionnaire questionnaire) {
        return jpaRepository.save(questionnaire);
    }
}
