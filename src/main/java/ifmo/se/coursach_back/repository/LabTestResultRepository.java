package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.model.LabTestResult;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabTestResultRepository extends JpaRepository<LabTestResult, UUID> {
    Optional<LabTestResult> findBySample_IdAndTestType_Id(UUID sampleId, Short testTypeId);
    
    List<LabTestResult> findBySample_Id(UUID sampleId);

    @Query("""
            select result
            from LabTestResult result
            join result.sample sample
            join sample.donation donation
            join donation.visit visit
            join visit.booking booking
            where booking.donor.account.id = :accountId
              and result.published = true
            order by result.testedAt desc
            """)
    List<LabTestResult> findPublishedByDonorAccountId(@Param("accountId") UUID accountId);
    
    @Query("SELECT COUNT(r) FROM LabTestResult r WHERE r.published = true AND r.testedAt >= :from AND r.testedAt <= :to")
    long countPublishedByTestedAtBetween(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);

    @Query("""
            select result
            from LabTestResult result
            join result.sample sample
            join sample.donation donation
            join donation.visit visit
            join visit.booking booking
            where booking.donor.id = :donorId
            order by result.testedAt desc
            """)
    List<LabTestResult> findByDonorId(@Param("donorId") UUID donorId);
}
