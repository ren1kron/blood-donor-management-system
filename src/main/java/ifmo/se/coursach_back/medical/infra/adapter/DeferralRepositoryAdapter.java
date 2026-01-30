package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.DeferralRepositoryPort;
import ifmo.se.coursach_back.medical.domain.Deferral;
import ifmo.se.coursach_back.medical.infra.jpa.DeferralRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeferralRepositoryAdapter implements DeferralRepositoryPort {
    private final DeferralRepository jpaRepository;

    @Override
    public Optional<Deferral> findActiveDeferral(UUID donorId, OffsetDateTime now) {
        return jpaRepository.findActiveDeferral(donorId, now);
    }

    @Override
    public Deferral save(Deferral deferral) {
        return jpaRepository.save(deferral);
    }
}
