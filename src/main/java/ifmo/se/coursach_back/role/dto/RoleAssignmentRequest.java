package ifmo.se.coursach_back.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RoleAssignmentRequest(
        @NotNull(message = "Account ID is required")
        UUID accountId,
        @NotBlank(message = "Role code is required")
        String roleCode
) {
}
