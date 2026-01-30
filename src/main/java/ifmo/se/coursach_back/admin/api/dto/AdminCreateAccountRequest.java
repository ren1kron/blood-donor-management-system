package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminCreateAccountRequest(
        String email,
        String phone,
        @NotBlank String password,
        Boolean isActive
) {
}
