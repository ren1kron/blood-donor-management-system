package ifmo.se.coursach_back.donor.infra.adapter;

import ifmo.se.coursach_back.donor.application.ports.DonorProfileRepositoryPort;
import ifmo.se.coursach_back.donor.application.ports.DonorSummary;
import ifmo.se.coursach_back.donor.domain.DonorProfile;
import ifmo.se.coursach_back.donor.domain.DonorStatus;
import ifmo.se.coursach_back.donor.infra.jpa.DonorProfileRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DonorProfileRepositoryAdapter implements DonorProfileRepositoryPort {
    private final DonorProfileRepository jpaRepository;

    @Override
    public Optional<DonorProfile> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<DonorProfile> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountId(accountId);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public long countByDonorStatus(DonorStatus donorStatus) {
        return jpaRepository.countByDonorStatus(donorStatus);
    }

    @Override
    public List<DonorProfile> findByDonorStatus(DonorStatus donorStatus) {
        return jpaRepository.findByDonorStatus(donorStatus);
    }

    @Override
    public List<? extends DonorSummary> findDonorSummaries() {
        return jpaRepository.findDonorSummaries();
    }

    @Override
    public DonorProfile save(DonorProfile donorProfile) {
        return jpaRepository.save(donorProfile);
    }
}
