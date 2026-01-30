package ifmo.se.coursach_back.lab.application.result;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Result object for a single lab test result.
 */
public record LabTestResultResult(
        UUID resultId,
        UUID sampleId,
        String testTypeCode,
        String testTypeName,
        String resultValue,
        String resultFlag,
        boolean published,
        OffsetDateTime testedAt,
        OffsetDateTime publishedAt
) {
}
