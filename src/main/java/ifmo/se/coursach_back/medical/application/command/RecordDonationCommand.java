package ifmo.se.coursach_back.medical.application.command;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Command for recording a donation.
 */
public record RecordDonationCommand(
        UUID accountId,
        UUID bookingId,
        UUID visitId,
        String donationType,
        Integer volumeMl,
        OffsetDateTime performedAt,
        String notes
) {
}
