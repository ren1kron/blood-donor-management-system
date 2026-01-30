package ifmo.se.coursach_back.medical.application.command;

import java.util.UUID;

/**
 * Command for registering a sample from a donation.
 */
public record RegisterSampleCommand(
        UUID donationId,
        String sampleCode,
        String status,
        String quarantineReason,
        String rejectionReason
) {
}
