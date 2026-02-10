package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.Deferral;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeferralRepository extends JpaRepository<Deferral, UUID> {
    @Query(value = """
            select d.*
            from deferral d
            where d.id = fn_get_active_deferral_id(:donorId, :now)
            """, nativeQuery = true)
    Optional<Deferral> findActiveDeferral(@Param("donorId") UUID donorId, @Param("now") OffsetDateTime now);
}
