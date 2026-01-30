package ifmo.se.coursach_back.donor.application.command;

import java.util.UUID;

/**
 * Command for submitting consent.
 */
public record SubmitConsentCommand(
        UUID accountId,
        UUID visitId,
        UUID bookingId,
        String consentType
) {
}
