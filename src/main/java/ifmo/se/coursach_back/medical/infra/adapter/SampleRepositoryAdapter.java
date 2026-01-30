package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.SampleRepositoryPort;
import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.infra.jpa.SampleRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SampleRepositoryAdapter implements SampleRepositoryPort {
    private final SampleRepository jpaRepository;

    @Override
    public Optional<Sample> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsBySampleCode(String sampleCode) {
        return jpaRepository.existsBySampleCode(sampleCode);
    }

    @Override
    public List<Sample> findByStatuses(List<String> statuses) {
        return jpaRepository.findByStatuses(statuses);
    }

    @Override
    public long countByCollectedAtBetween(OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.countByCollectedAtBetween(from, to);
    }

    @Override
    public List<Sample> findByDonorId(UUID donorId) {
        return jpaRepository.findByDonorId(donorId);
    }

    @Override
    public Sample save(Sample sample) {
        return jpaRepository.save(sample);
    }
}
