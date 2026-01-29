package ifmo.se.coursach_back.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdminCreateStaffProfileRequest(
        @NotNull UUID accountId,
        @NotBlank String fullName,
        @NotBlank String staffKind
) {
}
