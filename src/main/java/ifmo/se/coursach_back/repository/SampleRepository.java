package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.Sample;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SampleRepository extends JpaRepository<Sample, UUID> {
    boolean existsBySampleCode(String sampleCode);

    List<Sample> findByStatusInOrderByCollectedAtAsc(List<String> statuses);
    
    @Query("SELECT COUNT(s) FROM Sample s WHERE s.collectedAt >= :from AND s.collectedAt <= :to")
    long countByCollectedAtBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    @Query("""
            select sample
            from Sample sample
            join sample.donation donation
            join donation.visit visit
            join visit.booking booking
            where booking.donor.id = :donorId
            order by sample.collectedAt desc
            """)
    List<Sample> findByDonorId(@Param("donorId") UUID donorId);
}
