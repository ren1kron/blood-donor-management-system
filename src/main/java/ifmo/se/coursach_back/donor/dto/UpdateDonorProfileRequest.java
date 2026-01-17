package ifmo.se.coursach_back.donor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateDonorProfileRequest(
        @Size(max = 200) String fullName,
        LocalDate birthDate,
        String bloodGroup,
        String rhFactor,
        @Email String email,
        String phone
) {
}
