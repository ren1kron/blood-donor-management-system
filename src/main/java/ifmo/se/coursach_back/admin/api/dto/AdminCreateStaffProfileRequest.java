package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record AdminCreateStaffProfileRequest(
        @NotNull(message = "Account ID is required")
        UUID accountId,
        @NotBlank(message = "Full name is required")
        @Size(max = 200, message = "Full name must not exceed 200 characters")
        String fullName,
        @NotBlank(message = "Staff kind is required")
        String staffKind
) {
}
