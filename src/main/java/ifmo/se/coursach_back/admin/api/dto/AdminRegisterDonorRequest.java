package ifmo.se.coursach_back.admin.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AdminRegisterDonorRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull LocalDate birthDate,
        String bloodGroup,
        String rhFactor
) {
}
