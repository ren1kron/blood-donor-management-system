package ifmo.se.coursach_back.lab.infra.adapter;

import ifmo.se.coursach_back.lab.application.ports.LabTestResultRepositoryPort;
import ifmo.se.coursach_back.lab.domain.LabTestResult;
import ifmo.se.coursach_back.lab.infra.jpa.LabTestResultRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LabTestResultRepositoryAdapter implements LabTestResultRepositoryPort {
    private final LabTestResultRepository jpaRepository;

    @Override
    public Optional<LabTestResult> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<LabTestResult> findBySample_IdAndTestType_Id(UUID sampleId, Short testTypeId) {
        return jpaRepository.findBySample_IdAndTestType_Id(sampleId, testTypeId);
    }

    @Override
    public List<LabTestResult> findBySample_Id(UUID sampleId) {
        return jpaRepository.findBySample_Id(sampleId);
    }

    @Override
    public List<LabTestResult> findPublishedByDonorAccountId(UUID accountId) {
        return jpaRepository.findPublishedByDonorAccountId(accountId);
    }

    @Override
    public long countPublishedByTestedAtBetween(OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.countPublishedByTestedAtBetween(from, to);
    }

    @Override
    public List<LabTestResult> findByDonorId(UUID donorId) {
        return jpaRepository.findByDonorId(donorId);
    }

    @Override
    public LabTestResult save(LabTestResult result) {
        return jpaRepository.save(result);
    }
}
