package ifmo.se.coursach_back.admin.application.command;

import java.time.LocalDate;

/**
 * Command object for registering a donor by phone.
 */
public record RegisterDonorByPhoneCommand(
        String phone,
        String email,
        String password,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor
) {
}
