package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.lab.dto.PendingSampleProjection;
import ifmo.se.coursach_back.model.Sample;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SampleRepository extends JpaRepository<Sample, UUID> {
    boolean existsBySampleCode(String sampleCode);

    @Query(value = """
            select
                sample_id as sampleId,
                sample_code as sampleCode,
                status,
                collected_at as collectedAt,
                donation_id as donationId,
                donor_id as donorId,
                donor_full_name as donorFullName
            from fn_pending_samples(:statuses)
            """, nativeQuery = true)
    List<PendingSampleProjection> findPendingSamples(@Param("statuses") String[] statuses);
}
