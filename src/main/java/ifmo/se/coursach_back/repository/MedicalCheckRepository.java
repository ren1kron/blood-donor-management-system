package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.MedicalCheck;
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
    
    @Query("SELECT mc FROM MedicalCheck mc " +
           "WHERE mc.visit.booking.donor.id = :donorId " +
           "AND mc.decision = 'ADMITTED' " +
           "AND mc.decisionAt >= :since " +
           "ORDER BY mc.decisionAt DESC")
    List<MedicalCheck> findValidAdmittedChecksByDonorId(
            @Param("donorId") UUID donorId, 
            @Param("since") OffsetDateTime since);
}
