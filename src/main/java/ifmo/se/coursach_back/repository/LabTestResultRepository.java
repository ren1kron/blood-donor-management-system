package ifmo.se.coursach_back.repository;

import ifmo.se.coursach_back.donor.dto.LabResultProjection;
import ifmo.se.coursach_back.model.LabTestResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabTestResultRepository extends JpaRepository<LabTestResult, UUID> {
    Optional<LabTestResult> findBySample_IdAndTestType_Id(UUID sampleId, Short testTypeId);

    @Query(value = """
            select
                result_id as resultId,
                sample_id as sampleId,
                sample_code as sampleCode,
                test_type_id as testTypeId,
                test_type_code as testTypeCode,
                result_flag as resultFlag,
                result_value as resultValue,
                tested_at as testedAt,
                published_at as publishedAt
            from fn_published_lab_results(:accountId)
            """, nativeQuery = true)
    List<LabResultProjection> findPublishedByDonorAccountId(@Param("accountId") UUID accountId);
}
