package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.donor.dto.DeferralProjection;
import ifmo.se.coursach_back.model.Deferral;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeferralRepository extends JpaRepository<Deferral, UUID> {
    @Query(value = """
            select
                deferral_id as deferralId,
                deferral_type as deferralType,
                reason,
                starts_at as startsAt,
                ends_at as endsAt
            from fn_active_deferral(:donorId, :now)
            """, nativeQuery = true)
    Optional<DeferralProjection> findActiveDeferral(@Param("donorId") UUID donorId, @Param("now") OffsetDateTime now);
}
