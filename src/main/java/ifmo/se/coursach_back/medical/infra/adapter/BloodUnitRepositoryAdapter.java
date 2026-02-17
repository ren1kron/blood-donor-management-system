package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.BloodUnitRepositoryPort;
import ifmo.se.coursach_back.medical.domain.BloodUnit;
import ifmo.se.coursach_back.medical.infra.jpa.BloodUnitRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BloodUnitRepositoryAdapter implements BloodUnitRepositoryPort {
    private final BloodUnitRepository jpaRepository;

    @Override
    public Optional<BloodUnit> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<BloodUnit> findByStatuses(List<String> statuses) {
        return jpaRepository.findByStatuses(statuses);
    }

    @Override
    public List<BloodUnit> findByDonationId(UUID donationId) {
        return jpaRepository.findByDonationId(donationId);
    }

    @Override
    public BloodUnit save(BloodUnit bloodUnit) {
        return jpaRepository.save(bloodUnit);
    }
}
