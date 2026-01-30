package ifmo.se.coursach_back.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
        @Email String email,
        String phone,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotBlank String fullName,
        @NotNull LocalDate birthDate,
        String bloodGroup,
        String rhFactor
) {
}
