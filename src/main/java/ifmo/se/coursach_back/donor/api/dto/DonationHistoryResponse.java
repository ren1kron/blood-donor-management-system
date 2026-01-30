package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.nurse.domain.CollectionSession;
import ifmo.se.coursach_back.medical.domain.Donation;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonationHistoryResponse(
        UUID donationId,
        UUID visitId,
        OffsetDateTime performedAt,
        String donationType,
        Integer volumeMl,
        OffsetDateTime publishedAt,
        String preVitalsJson,
        String postVitalsJson,
        boolean hasVitals
) {
    public static DonationHistoryResponse from(Donation donation, CollectionSession session) {
        String preVitals = session != null ? session.getPreVitalsJson() : null;
        String postVitals = session != null ? session.getPostVitalsJson() : null;
        boolean hasVitals = preVitals != null || postVitals != null;
        
        return new DonationHistoryResponse(
                donation.getId(),
                donation.getVisit().getId(),
                donation.getPerformedAt(),
                donation.getDonationType() != null ? donation.getDonationType().getValue() : null,
                donation.getVolumeMl(),
                donation.getPublishedAt(),
                preVitals,
                postVitals,
                hasVitals
        );
    }
}
