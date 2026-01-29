package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.CollectionSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionSessionRepository extends JpaRepository<CollectionSession, UUID> {
    Optional<CollectionSession> findByVisit_Id(UUID visitId);

    List<CollectionSession> findByVisit_IdIn(List<UUID> visitIds);
}
