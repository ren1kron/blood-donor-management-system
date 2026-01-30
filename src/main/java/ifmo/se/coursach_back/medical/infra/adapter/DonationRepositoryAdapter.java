package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.admin.api.dto.EligibleDonorRow;
import ifmo.se.coursach_back.medical.application.ports.DonationRepositoryPort;
import ifmo.se.coursach_back.medical.domain.Donation;
import ifmo.se.coursach_back.medical.infra.jpa.DonationRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DonationRepositoryAdapter implements DonationRepositoryPort {
    private final DonationRepository jpaRepository;

    @Override
    public Optional<Donation> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Donation> findByVisit_Id(UUID visitId) {
        return jpaRepository.findByVisit_Id(visitId);
    }

    @Override
    public List<EligibleDonorRow> findEligibleDonors(OffsetDateTime threshold) {
        return jpaRepository.findEligibleDonors(threshold);
    }

    @Override
    public List<Donation> findByDonorAccountId(UUID accountId) {
        return jpaRepository.findByDonorAccountId(accountId);
    }

    @Override
    public List<Donation> findPublishedByDonorAccountId(UUID accountId) {
        return jpaRepository.findPublishedByDonorAccountId(accountId);
    }

    @Override
    public Optional<Donation> findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(UUID accountId) {
        return jpaRepository.findTopByVisit_Booking_Donor_Account_IdOrderByPerformedAtDesc(accountId);
    }

    @Override
    public List<Donation> findByVisit_IdIn(List<UUID> visitIds) {
        return jpaRepository.findByVisit_IdIn(visitIds);
    }

    @Override
    public long countByPerformedAtBetween(OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.countByPerformedAtBetween(from, to);
    }

    @Override
    public List<Object[]> sumVolumeByBloodTypeAndRh(OffsetDateTime from, OffsetDateTime to) {
        return jpaRepository.sumVolumeByBloodTypeAndRh(from, to);
    }

    @Override
    public Donation save(Donation donation) {
        return jpaRepository.save(donation);
    }
}
