package ifmo.se.coursach_back.donor.infra.adapter;

import ifmo.se.coursach_back.donor.application.ports.ConsentRepositoryPort;
import ifmo.se.coursach_back.donor.domain.Consent;
import ifmo.se.coursach_back.donor.infra.jpa.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConsentRepositoryAdapter implements ConsentRepositoryPort {
    private final ConsentRepository jpaRepository;

    @Override
    public Consent save(Consent consent) {
        return jpaRepository.save(consent);
    }
}
