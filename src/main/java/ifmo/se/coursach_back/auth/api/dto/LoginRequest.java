package ifmo.se.coursach_back.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email or phone is required")
        String identifier,
        @NotBlank(message = "Password is required")
        String password
) {
}
