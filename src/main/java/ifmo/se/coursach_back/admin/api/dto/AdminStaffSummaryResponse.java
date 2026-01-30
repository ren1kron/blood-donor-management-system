package ifmo.se.coursach_back.admin.api.dto;

import java.util.List;
import java.util.UUID;

public record AdminStaffSummaryResponse(
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
