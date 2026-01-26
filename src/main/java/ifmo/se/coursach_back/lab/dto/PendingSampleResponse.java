package ifmo.se.coursach_back.lab.dto;

import ifmo.se.coursach_back.model.Sample;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PendingSampleResponse(
        UUID sampleId,
        String sampleCode,
        String status,
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
                sample.getDonation().getVisit().getBooking().getDonor().getAccount().getFullName()
        );
    }

    public static PendingSampleResponse fromProjection(PendingSampleProjection projection) {
        return new PendingSampleResponse(
                projection.getSampleId(),
                projection.getSampleCode(),
                projection.getStatus(),
                projection.getCollectedAt(),
                projection.getDonationId(),
                projection.getDonorId(),
                projection.getDonorFullName()
        );
    }
}
