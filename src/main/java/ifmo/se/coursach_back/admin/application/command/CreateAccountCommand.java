package ifmo.se.coursach_back.admin.application.command;

import java.util.UUID;

/**
 * Command object for creating a new account by an admin.
 */
public record CreateAccountCommand(
        UUID adminAccountId,
        String email,
        String phone,
        String password,
        Boolean isActive
) {
}
