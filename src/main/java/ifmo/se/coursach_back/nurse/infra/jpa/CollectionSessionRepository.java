package ifmo.se.coursach_back.nurse.infra.jpa;

import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollectionSessionRepository extends JpaRepository<CollectionSession, UUID> {
    @Query("select cs from CollectionSession cs where cs.visit.id = :visitId")
    Optional<CollectionSession> findByVisitId(@Param("visitId") UUID visitId);

    @EntityGraph(attributePaths = {"visit", "nurse"})
    @Query("select cs from CollectionSession cs where cs.visit.id in :visitIds")
    List<CollectionSession> findByVisitIds(@Param("visitIds") List<UUID> visitIds);
}
