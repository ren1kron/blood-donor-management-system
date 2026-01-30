package ifmo.se.coursach_back.admin.application.result;

import java.util.List;
import java.util.UUID;

/**
 * Result object for staff summary.
 */
public record StaffSummaryResult(
        UUID staffId,
        UUID accountId,
        String fullName,
        String staffKind,
        String email,
        String phone,
        boolean isActive,
        List<String> roles
) {
}
