package ifmo.se.coursach_back.donor.infra.jpa;

import ifmo.se.coursach_back.donor.domain.Consent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<Consent, UUID> {
}
