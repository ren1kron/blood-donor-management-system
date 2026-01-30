package ifmo.se.coursach_back.auth.application.command;

import java.time.LocalDate;

/**
 * Command object for user registration use case.
 */
public record RegisterUserCommand(
        String email,
        String phone,
        String password,
        String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor
) {
}
