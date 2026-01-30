package ifmo.se.coursach_back.admin.application.command;

import java.util.UUID;

/**
 * Command object for creating a staff profile.
 */
public record CreateStaffProfileCommand(
        UUID adminAccountId,
        UUID accountId,
        String fullName,
        String staffKind
) {
}
