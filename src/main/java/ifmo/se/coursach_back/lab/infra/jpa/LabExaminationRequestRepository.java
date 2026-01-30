package ifmo.se.coursach_back.lab.infra.jpa;

import ifmo.se.coursach_back.lab.domain.LabExaminationRequest;
import ifmo.se.coursach_back.lab.domain.LabExaminationStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabExaminationRequestRepository extends JpaRepository<LabExaminationRequest, UUID> {
    @Query("select r from LabExaminationRequest r where r.visit.id = :visitId")
    Optional<LabExaminationRequest> findByVisitId(@Param("visitId") UUID visitId);

    @Query("select r from LabExaminationRequest r where r.visit.id in :visitIds")
    List<LabExaminationRequest> findByVisitIds(@Param("visitIds") List<UUID> visitIds);

    @Query("""
            select distinct r
            from LabExaminationRequest r
            join fetch r.visit v
            join fetch v.booking b
            join fetch b.donor d
            join fetch b.slot s
            where r.status in :statuses
            order by r.requestedAt asc
            """)
    List<LabExaminationRequest> findByStatuses(
            @Param("statuses") List<LabExaminationStatus> statuses);

    long countByStatusIn(List<LabExaminationStatus> statuses);
}
