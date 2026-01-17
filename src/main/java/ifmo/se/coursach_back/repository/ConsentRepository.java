package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Consent;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<Consent, UUID> {
}
