package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.MedicalCheck;
import ifmo.se.coursach_back.medical.domain.MedicalCheckDecision;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalCheckRepository extends JpaRepository<MedicalCheck, UUID> {
    @Query("select mc from MedicalCheck mc where mc.visit.id = :visitId")
    Optional<MedicalCheck> findByVisitId(@Param("visitId") UUID visitId);
    
    @Query("select mc from MedicalCheck mc where mc.visit.id in :visitIds")
    List<MedicalCheck> findByVisitIds(@Param("visitIds") List<UUID> visitIds);
    
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

    @Query("""
            select mc
            from MedicalCheck mc
            join mc.visit v
            join v.booking b
            where b.donor.id = :donorId
            order by mc.decisionAt desc
            """)
    List<MedicalCheck> findLatestByDonorId(@Param("donorId") UUID donorId, Pageable pageable);

    default Optional<MedicalCheck> findLatestByDonorId(UUID donorId) {
        return findLatestByDonorId(donorId, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    @Query("SELECT mc FROM MedicalCheck mc " +
           "WHERE mc.visit.booking.donor.id = :donorId " +
           "ORDER BY mc.decisionAt DESC")
    List<MedicalCheck> findByDonorId(@Param("donorId") UUID donorId);
}
