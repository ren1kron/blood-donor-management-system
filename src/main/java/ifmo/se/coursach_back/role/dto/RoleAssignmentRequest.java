package ifmo.se.coursach_back.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RoleAssignmentRequest(
        @NotNull UUID accountId,
        @NotBlank String roleCode
) {
}
