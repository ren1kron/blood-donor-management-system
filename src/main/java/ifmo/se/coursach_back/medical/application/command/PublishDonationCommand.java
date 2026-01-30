package ifmo.se.coursach_back.medical.application.command;

import java.util.UUID;

/**
 * Command for publishing a donation.
 */
public record PublishDonationCommand(
        UUID accountId,
        UUID donationId
) {
}
