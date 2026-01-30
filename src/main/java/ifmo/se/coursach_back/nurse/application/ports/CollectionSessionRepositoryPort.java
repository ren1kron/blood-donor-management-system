package ifmo.se.coursach_back.nurse.application.ports;

import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port interface for CollectionSession repository operations.
 * Application layer depends on this interface, not on JPA repository.
 */
public interface CollectionSessionRepositoryPort {
    Optional<CollectionSession> findById(UUID id);
    Optional<CollectionSession> findByVisit_Id(UUID visitId);
    List<CollectionSession> findByVisit_IdIn(List<UUID> visitIds);
    CollectionSession save(CollectionSession session);
}
