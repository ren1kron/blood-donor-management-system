package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.AdverseReaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdverseReactionRepository extends JpaRepository<AdverseReaction, UUID> {
}
