package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.Size;

public record AdminUpdateAccountRequest(
        Boolean isActive,
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String newPassword
) {
}
