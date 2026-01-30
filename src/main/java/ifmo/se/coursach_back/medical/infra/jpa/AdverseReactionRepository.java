package ifmo.se.coursach_back.medical.infra.jpa;

import ifmo.se.coursach_back.medical.domain.AdverseReaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdverseReactionRepository extends JpaRepository<AdverseReaction, UUID> {
}
