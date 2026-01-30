package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalCheckRepository extends JpaRepository<MedicalCheck, UUID> {
    Optional<MedicalCheck> findByVisit_Id(UUID visitId);
    
    List<MedicalCheck> findByVisit_IdIn(List<UUID> visitIds);
    
    List<MedicalCheck> findByStatusOrderBySubmittedAtAsc(MedicalCheckDecision status);

    long countByStatus(MedicalCheckDecision status);
    
    @Query("SELECT mc FROM MedicalCheck mc " +
           "WHERE mc.visit.booking.donor.id = :donorId " +
           "AND mc.decision = :decision " +
           "AND mc.decisionAt >= :since " +
           "ORDER BY mc.decisionAt DESC")
    List<MedicalCheck> findValidAdmittedChecksByDonorId(
            @Param("donorId") UUID donorId,
            @Param("decision") MedicalCheckDecision decision,
            @Param("since") OffsetDateTime since);

    Optional<MedicalCheck> findTopByVisit_Booking_Donor_IdOrderByDecisionAtDesc(UUID donorId);

    @Query("SELECT mc FROM MedicalCheck mc " +
           "WHERE mc.visit.booking.donor.id = :donorId " +
           "ORDER BY mc.decisionAt DESC")
    List<MedicalCheck> findByDonorId(@Param("donorId") UUID donorId);
}
