package ifmo.se.coursach_back.donor.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateDonorProfileRequest(
        @Size(max = 200, message = "Full name must not exceed 200 characters")
        String fullName,
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        @Email(message = "Invalid email format")
        String email,
        String phone
) {
}
