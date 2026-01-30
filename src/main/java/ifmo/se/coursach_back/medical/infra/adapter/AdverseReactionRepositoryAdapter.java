package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.AdverseReactionRepositoryPort;
import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import ifmo.se.coursach_back.medical.infra.jpa.AdverseReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AdverseReactionRepositoryAdapter implements AdverseReactionRepositoryPort {
    private final AdverseReactionRepository jpaRepository;

    @Override
    public AdverseReaction save(AdverseReaction adverseReaction) {
        return jpaRepository.save(adverseReaction);
    }
}
