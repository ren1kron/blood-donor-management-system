package ifmo.se.coursach_back.role.dto;

import java.util.List;
import java.util.UUID;

public record AccountRolesResponse(
        UUID accountId,
        List<String> roles
) {
}
