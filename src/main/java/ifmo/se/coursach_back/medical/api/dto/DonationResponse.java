package ifmo.se.coursach_back.medical.api.dto;

import ifmo.se.coursach_back.appointment.domain.Booking;
import ifmo.se.coursach_back.medical.domain.Donation;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DonationResponse(
        UUID id,
        UUID visitId,
        UUID bookingId,
        UUID donorId,
        String donationType,
        Integer volumeMl,
        OffsetDateTime performedAt,
        boolean published,
        OffsetDateTime publishedAt
) {
    public static DonationResponse from(Donation donation, Booking booking) {
        return new DonationResponse(
                donation.getId(),
                donation.getVisit().getId(),
                booking.getId(),
                booking.getDonor().getId(),
                donation.getDonationType() != null ? donation.getDonationType().getValue() : null,
                donation.getVolumeMl(),
                donation.getPerformedAt(),
                donation.isPublished(),
                donation.getPublishedAt()
        );
    }
}
