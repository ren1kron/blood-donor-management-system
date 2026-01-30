package ifmo.se.coursach_back.medical.infra.adapter;

import ifmo.se.coursach_back.medical.application.ports.MedicalCheckRepositoryPort;
import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import ifmo.se.coursach_back.medical.infra.jpa.MedicalCheckRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MedicalCheckRepositoryAdapter implements MedicalCheckRepositoryPort {
    private final MedicalCheckRepository jpaRepository;

    @Override
    public Optional<MedicalCheck> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<MedicalCheck> findByVisit_Id(UUID visitId) {
        return jpaRepository.findByVisit_Id(visitId);
    }

    @Override
    public List<MedicalCheck> findByVisit_IdIn(List<UUID> visitIds) {
        return jpaRepository.findByVisit_IdIn(visitIds);
    }

    @Override
    public List<MedicalCheck> findByStatusOrderBySubmittedAtAsc(MedicalCheckDecision status) {
        return jpaRepository.findByStatusOrderBySubmittedAtAsc(status);
    }

    @Override
    public long countByStatus(MedicalCheckDecision status) {
        return jpaRepository.countByStatus(status);
    }

    @Override
    public List<MedicalCheck> findValidAdmittedChecksByDonorId(UUID donorId, MedicalCheckDecision decision, OffsetDateTime since) {
        return jpaRepository.findValidAdmittedChecksByDonorId(donorId, decision, since);
    }

    @Override
    public Optional<MedicalCheck> findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(UUID donorId) {
        return jpaRepository.findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(donorId);
    }

    @Override
    public List<MedicalCheck> findByDonorId(UUID donorId) {
        return jpaRepository.findByDonorId(donorId);
    }

    @Override
    public MedicalCheck save(MedicalCheck medicalCheck) {
        return jpaRepository.save(medicalCheck);
    }
}
