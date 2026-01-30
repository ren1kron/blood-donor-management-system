package ifmo.se.coursach_back.lab.dto;

import ifmo.se.coursach_back.model.Sample;
import ifmo.se.coursach_back.model.SampleStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PendingSampleResponse(
        UUID sampleId,
        String sampleCode,
        SampleStatus status,
        OffsetDateTime collectedAt,
        UUID donationId,
        UUID donorId,
        String donorFullName
) {
    public static PendingSampleResponse from(Sample sample) {
        return new PendingSampleResponse(
                sample.getId(),
                sample.getSampleCode(),
                sample.getStatus(),
                sample.getCollectedAt(),
                sample.getDonation().getId(),
                sample.getDonation().getVisit().getBooking().getDonor().getId(),
                sample.getDonation().getVisit().getBooking().getDonor().getFullName()
        );
    }
}
