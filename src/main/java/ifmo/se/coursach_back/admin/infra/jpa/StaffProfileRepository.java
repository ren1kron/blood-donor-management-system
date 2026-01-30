package ifmo.se.coursach_back.admin.infra.jpa;

import ifmo.se.coursach_back.admin.domain.StaffProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, UUID> {
    Optional<StaffProfile> findByAccountId(UUID accountId);
}
