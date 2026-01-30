package ifmo.se.coursach_back.donor.application.command;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command for updating donor profile.
 */
public record UpdateDonorProfileCommand(
        UUID accountId,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        String email,
        String phone
) {
}
