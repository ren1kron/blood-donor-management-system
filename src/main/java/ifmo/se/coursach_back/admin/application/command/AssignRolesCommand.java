package ifmo.se.coursach_back.admin.application.command;

import java.util.List;
import java.util.UUID;

/**
 * Command object for assigning roles to an account.
 */
public record AssignRolesCommand(
        UUID adminAccountId,
        UUID accountId,
        List<String> roles
) {
}
