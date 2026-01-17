package ifmo.se.coursach_back.donor.dto;

import ifmo.se.coursach_back.model.Donation;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonationHistoryResponse(
        UUID donationId,
        UUID visitId,
        OffsetDateTime performedAt,
        String donationType,
        Integer volumeMl
) {
    public static DonationHistoryResponse from(Donation donation) {
        return new DonationHistoryResponse(
                donation.getId(),
                donation.getVisit().getId(),
                donation.getPerformedAt(),
                donation.getDonationType(),
                donation.getVolumeMl()
        );
    }
}
