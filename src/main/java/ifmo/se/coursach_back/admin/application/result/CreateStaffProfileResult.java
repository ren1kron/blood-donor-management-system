package ifmo.se.coursach_back.admin.application.result;

import java.util.UUID;

/**
 * Result object for staff profile creation.
 */
public record CreateStaffProfileResult(
        UUID staffProfileId
) {
}
