package ifmo.se.coursach_back.medical.application.ports;

import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for MedicalCheck repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface MedicalCheckRepositoryPort {
    Optional<MedicalCheck> findById(UUID id);
    Optional<MedicalCheck> findByVisit_Id(UUID visitId);
    List<MedicalCheck> findByVisit_IdIn(List<UUID> visitIds);
    List<MedicalCheck> findByStatusOrderBySubmittedAtAsc(MedicalCheckDecision status);
    long countByStatus(MedicalCheckDecision status);
    List<MedicalCheck> findValidAdmittedChecksByDonorId(UUID donorId, MedicalCheckDecision decision, OffsetDateTime since);
    Optional<MedicalCheck> findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(UUID donorId);
    List<MedicalCheck> findByDonorId(UUID donorId);
    MedicalCheck save(MedicalCheck medicalCheck);
}
