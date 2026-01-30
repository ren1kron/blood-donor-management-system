package ifmo.se.coursach_back.admin.application.command;

import java.util.UUID;

/**
 * Command object for updating an account.
 */
public record UpdateAccountCommand(
        UUID adminAccountId,
        UUID accountId,
        Boolean isActive,
        String newPassword
) {
}
