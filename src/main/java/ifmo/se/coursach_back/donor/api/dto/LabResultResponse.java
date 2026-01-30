package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.lab.domain.LabTestResult;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LabResultResponse(
        UUID resultId,
        UUID sampleId,
        String sampleCode,
        Short testTypeId,
        String testTypeCode,
        String resultFlag,
        String resultValue,
        OffsetDateTime testedAt,
        OffsetDateTime publishedAt
) {
    public static LabResultResponse from(LabTestResult result) {
        return new LabResultResponse(
                result.getId(),
                result.getSample().getId(),
                result.getSample().getSampleCode(),
                result.getTestType().getId(),
                result.getTestType().getCode(),
                result.getResultFlag(),
                result.getResultValue(),
                result.getTestedAt(),
                result.getPublishedAt()
        );
    }
}
