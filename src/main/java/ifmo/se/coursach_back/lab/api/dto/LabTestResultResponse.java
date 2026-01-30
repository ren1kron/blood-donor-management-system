package ifmo.se.coursach_back.lab.api.dto;

import ifmo.se.coursach_back.lab.domain.LabTestResult;
import java.time.OffsetDateTime;
import java.util.UUID;

public record LabTestResultResponse(
        UUID id,
        UUID sampleId,
        Short testTypeId,
        String testTypeCode,
        String resultValue,
        String resultFlag,
        OffsetDateTime testedAt,
        boolean published,
        OffsetDateTime publishedAt
) {
    public static LabTestResultResponse from(LabTestResult result) {
        return new LabTestResultResponse(
                result.getId(),
                result.getSample().getId(),
                result.getTestType().getId(),
                result.getTestType().getCode(),
                result.getResultValue(),
                result.getResultFlag(),
                result.getTestedAt(),
                result.isPublished(),
                result.getPublishedAt()
        );
    }
}
