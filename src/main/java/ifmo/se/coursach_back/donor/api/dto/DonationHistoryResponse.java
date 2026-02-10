package ifmo.se.coursach_back.donor.api.dto;

import ifmo.se.coursach_back.nurse.api.dto.VitalsPayload;
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
        VitalsPayload preVitals,
        VitalsPayload postVitals,
        boolean hasVitals
) {
    public static DonationHistoryResponse from(Donation donation, CollectionSession session) {
        VitalsPayload preVitals = null;
        VitalsPayload postVitals = null;
        if (session != null) {
            preVitals = buildVitals(session.getPreSystolicMmhg(), session.getPreDiastolicMmhg(),
                    session.getPrePulseRate(), session.getPreBodyTemperatureC(), session.getPreWellbeing());
            postVitals = buildVitals(session.getPostSystolicMmhg(), session.getPostDiastolicMmhg(),
                    session.getPostPulseRate(), session.getPostBodyTemperatureC(), session.getPostWellbeing());
        }
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

    private static VitalsPayload buildVitals(Integer systolic, Integer diastolic, Integer pulse,
                                             java.math.BigDecimal temperature, String wellbeing) {
        if (systolic == null && diastolic == null && pulse == null && temperature == null && wellbeing == null) {
            return null;
        }
        return new VitalsPayload(systolic, diastolic, pulse,
                temperature != null ? temperature.doubleValue() : null, wellbeing);
    }
}
