package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.medical.domain.Sample;
import ifmo.se.coursach_back.medical.domain.SampleStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record SampleResponse(
        UUID id,
        UUID donationId,
        String sampleCode,
        SampleStatus status,
        OffsetDateTime collectedAt
) {
    public static SampleResponse from(Sample sample) {
        return new SampleResponse(
                sample.getId(),
                sample.getDonation().getId(),
                sample.getSampleCode(),
                sample.getStatus(),
                sample.getCollectedAt()
        );
    }
}
