package ifmo.se.coursach_back.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
        @Email(message = "Invalid email format")
        String email,
        String phone,
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,
        @NotBlank(message = "Full name is required")
        @Size(max = 200, message = "Full name must not exceed 200 characters")
        String fullName,
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor
) {
}
