package ifmo.se.coursach_back.nurse.infra.adapter;

import ifmo.se.coursach_back.nurse.application.ports.CollectionSessionRepositoryPort;
import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.nurse.infra.jpa.CollectionSessionRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CollectionSessionRepositoryAdapter implements CollectionSessionRepositoryPort {
    private final CollectionSessionRepository jpaRepository;

    @Override
    public Optional<CollectionSession> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<CollectionSession> findByVisitId(UUID visitId) {
        return jpaRepository.findByVisitId(visitId);
    }

    @Override
    public List<CollectionSession> findByVisitIds(List<UUID> visitIds) {
        return jpaRepository.findByVisitIds(visitIds);
    }

    @Override
    public CollectionSession save(CollectionSession session) {
        return jpaRepository.save(session);
    }
}
