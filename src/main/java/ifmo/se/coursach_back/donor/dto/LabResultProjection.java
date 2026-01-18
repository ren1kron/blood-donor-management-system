package ifmo.se.coursach_back.donor.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface LabResultProjection {
    UUID getResultId();

    UUID getSampleId();

    String getSampleCode();

    Short getTestTypeId();

    String getTestTypeCode();

    String getResultFlag();

    String getResultValue();

    OffsetDateTime getTestedAt();

    OffsetDateTime getPublishedAt();
}
